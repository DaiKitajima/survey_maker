package jp.co.SurveyMaker.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import jp.co.SurveyMaker.Dto.AnswerContentDto;
import jp.co.SurveyMaker.Dto.AnswerPointDto;
import jp.co.SurveyMaker.Dto.AnswerResultDto;
import jp.co.SurveyMaker.Dto.CategoryContentDto;
import jp.co.SurveyMaker.Dto.SummaryResultDto;
import jp.co.SurveyMaker.Dto.SurveyCategoryResultDto;
import jp.co.SurveyMaker.Repository.SurveyCategory.SurveyCategoryRepository;
import jp.co.SurveyMaker.Repository.SurveyManagement.SurveyManagementRepository;
import jp.co.SurveyMaker.Repository.SurveyQuestion.SurveyQuestionRepository;
import jp.co.SurveyMaker.Repository.SurveyResult.SurveyResultRepository;
import jp.co.SurveyMaker.Service.Entity.SurveyCategory;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestion;
import jp.co.SurveyMaker.Service.Entity.SurveyResult;
import jp.co.SurveyMaker.Util.UUIDUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class SurveySimulationService {
	@Autowired
	private  SurveyManagementRepository surveyManagementRepository;
	 
	@Autowired
	private  SurveyQuestionRepository surveyQuestionRepository;

	@Autowired
	private  SurveyResultRepository surveyResultRepository;

	@Autowired
	private  SurveyCategoryRepository surveyCategoryRepository;
	
	// 診断結果登録
	public Integer surveyResultRegist(List<AnswerResultDto> answerResultLst) throws Exception{
		List<AnswerPointDto> categoryPointLst = new ArrayList<AnswerPointDto>(); 
		Integer contentId = -1;
		for(AnswerResultDto result : answerResultLst) {
			SurveyQuestion question = surveyQuestionRepository.findByIdAndDeleteFlgFalse(result.getQuestionId()).orElseThrow();
			contentId = question.getSurveyManagementId();
			
			Type listType = new TypeToken<ArrayList<AnswerContentDto>>(){}.getType();
			List<AnswerContentDto> answerLst = (new Gson()).fromJson(question.getAnswerContent(), listType);
			answerLst.forEach(answer ->{
				if(answer.getAnswerId().equals(result.getAnswerId())) {
					categoryPointLst.addAll(answer.getAnswerPointLst());
				}
			});
		}
		
		// カテゴリー別でトタル―ポイントを計算
		Map<Integer, Integer> categorySumMap = categoryPointLst.stream()
																					.collect(Collectors.groupingBy(AnswerPointDto::getCategoryId, Collectors.summingInt(AnswerPointDto::getPoint)));
		
		// 結果登録
		SurveyResult surveyResult = new SurveyResult();
		surveyResult.setSurveyManagementId(contentId);
		surveyResult.setSurveyResultContent(this.makeSurveyResultContent(categorySumMap));
		surveyResult.setSummaryResultContent(this.makeSurveySummaryResult(categorySumMap));
		surveyResult.setExpireDate(null);
		surveyResult.setSurveyKey(UUIDUtil.get32UUID());
		surveyResultRepository.save(surveyResult);
		
		return surveyResultRepository.getLastInsertId();
	}

	// 総合評価結果コンテンツ作成
	private String makeSurveySummaryResult(Map<Integer, Integer> categorySumMap) {
		SummaryResultDto summaryResult = new SummaryResultDto();
		Integer totalPoint = 0;
		for(Integer key : categorySumMap.keySet()) {
			totalPoint = totalPoint + categorySumMap.get(key);
		}
		summaryResult.setTotalPoint(totalPoint);
		summaryResult.setTopCategoryId(categorySumMap.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey());
		
		return (new Gson()).toJson(summaryResult);
	}

	// 評価結果コンテンツ作成
	private String makeSurveyResultContent(Map<Integer, Integer> categorySumMap) {
		List<SurveyCategoryResultDto>  categoryResultLst = new ArrayList<SurveyCategoryResultDto>();
		for(Integer categoryId : categorySumMap.keySet()) {
			Integer categoryPoint = categorySumMap.get(categoryId);
			SurveyCategory  category = surveyCategoryRepository.findByIdAndDeleteFlgFalse(categoryId).orElseThrow();
			Type listType = new TypeToken<ArrayList<CategoryContentDto>>(){}.getType();
			List<CategoryContentDto> resultLst = (new Gson()).fromJson(category.getSurveyCategoryContent(), listType);
			
			SurveyCategoryResultDto categoryResult = new SurveyCategoryResultDto();
			categoryResult.setCategoryId(categoryId);
			categoryResult.setCategoryName(category.getSurveyCategoryName());
			categoryResult.setCategoryTotalPoint(categoryPoint);
			resultLst.forEach(result ->{
				if(result.getPointFrom() <= categoryPoint && categoryPoint <= result.getPointTo() ) {
					categoryResult.setCategoryResultId(result.getId());
				}
			});
			categoryResultLst.add(categoryResult);
		}
		return (new Gson()).toJson(categoryResultLst);
	}
	
	// IDより診断結果取得
	public SurveyResult getSurveyResultById(Integer resultId)throws Exception {
		return surveyResultRepository.findByIdAndDeleteFlgFalse(resultId).orElseThrow();
	}
	
	// Keyより診断結果取得
	public SurveyResult getSurveyResultByKey(String key)throws Exception {
		return surveyResultRepository.findBySurveyKeyAndDeleteFlgFalse(key).orElseThrow();
	}

	// フローの場合、診断結果登録
	public Integer surveyResultRegistForFlow(Integer contentId, Integer categoryResultId)throws Exception {
		SurveyCategory  category = surveyCategoryRepository.findBySurveyManagementIdAndDeleteFlgFalse(contentId).get(0);
		// 結果登録
		SurveyResult surveyResult = new SurveyResult();
		surveyResult.setSurveyManagementId(contentId);
		surveyResult.setSurveyResultContent(this.makeSurveyResultContentForFlow(category, categoryResultId));
		surveyResult.setSummaryResultContent(null);
		surveyResult.setExpireDate(null);
		surveyResult.setSurveyKey(UUIDUtil.get32UUID());
		surveyResultRepository.save(surveyResult);
		
		return surveyResultRepository.getLastInsertId();
	}
	
	// フローの場合、カテゴリー評価結果作成
	private String makeSurveyResultContentForFlow(SurveyCategory category, Integer categoryResultId) {
		List<SurveyCategoryResultDto>  categoryResultLst = new ArrayList<SurveyCategoryResultDto>();

		Type listType = new TypeToken<ArrayList<CategoryContentDto>>(){}.getType();
		List<CategoryContentDto> resultLst = (new Gson()).fromJson(category.getSurveyCategoryContent(), listType);
		
		SurveyCategoryResultDto categoryResult = new SurveyCategoryResultDto();
		categoryResult.setCategoryId(category.getId());
		categoryResult.setCategoryName(category.getSurveyCategoryName());
		categoryResult.setCategoryTotalPoint(null);
		resultLst.forEach(result ->{
			if(result.getId().equals(categoryResultId) ) {
				categoryResult.setCategoryResultId(result.getId());
			}
		});
		categoryResultLst.add(categoryResult);

		return (new Gson()).toJson(categoryResultLst);
	}
	
}

package jp.co.SurveyMaker.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.SurveyMaker.Constants.CommonConstants;
import jp.co.SurveyMaker.Constants.LinkType;
import jp.co.SurveyMaker.Dto.AnswerContentDto;
import jp.co.SurveyMaker.Dto.CategoryContentDto;
import jp.co.SurveyMaker.Form.QuestionContentUpdateForm;
import jp.co.SurveyMaker.Form.QuestionLinkForm;
import jp.co.SurveyMaker.Form.SurveyCategoryUpdateForm;
import jp.co.SurveyMaker.Repository.SurveyQuestionLink.SurveyQuestionLinkRepository;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestionLink;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class SurveyQuestionLinkService {
	@Autowired
	private  SurveyQuestionLinkRepository surveyQuestionLinkRepository;
	
	// 質問リンク情報登録
	public Integer surveyQuestionLinkRegist(SurveyQuestionLink questionLink) throws Exception {
		surveyQuestionLinkRepository.save(questionLink);
		return surveyQuestionLinkRepository.getLastInsertId();
	}
	
	// 質問リンク情報更新
	public void surveyQuestionLinkUpdate(SurveyQuestionLink questionLink) throws Exception {
		surveyQuestionLinkRepository.save(questionLink);
	}
	
	// 質問リンク情報リスト更新
	public void surveyQuestionLinkLstUpdate(List<SurveyQuestionLink> questionLinkLst) throws Exception {
		if(questionLinkLst != null && questionLinkLst.size()!=0 ) {
			for(SurveyQuestionLink link: questionLinkLst) {
				try {
					this.surveyQuestionLinkUpdate(link);
				} catch (Exception e) {
					throw e;
				}
			}
		}
	}
	
	// 質問リンク情報取得
	public List<SurveyQuestionLink> getSurveyQuestionLinkLst(Integer contentId) throws Exception {
		return surveyQuestionLinkRepository.findBySurveyManagementIdAndDeleteFlgFalse(contentId);
	}
	
	// 指定質問のリンク情報取得
	public List<SurveyQuestionLink> getSurveyQuestionLinkLstByQuestionId(Integer contentId, Integer questionId) throws Exception {
		return surveyQuestionLinkRepository.findBySurveyManagementIdAndSurveyQuestionIdAndDeleteFlgFalse(contentId, questionId);
	}
	
	// 質問リンク情報削除(コンテンツIDと質問IDより)
	public void deleteQuestionLinkByContentIdAndQuestionId(Integer contentId,Integer questionId) throws Exception {
		surveyQuestionLinkRepository.deleteBySurveyManagementIdAndSurveyQuestionId(contentId, questionId);
	}

	// 質問リンク情報削除(コンテンツIDと質問リンクIDより)
	public void deleteQuestionLinkByContentIdAndLinkId(Integer contentId,Integer linkId) throws Exception {
		surveyQuestionLinkRepository.deleteBySurveyManagementIdAndId(contentId, linkId);
	}
	
	// チャートデータ作成
	public String makeFlowchartData(List<QuestionContentUpdateForm> questionFormLst, List<QuestionLinkForm> linkFormLst, SurveyCategoryUpdateForm category) {
		// チャートデータ構造
		/*
		 * { 
		 * "operators": { 
		 * "operator1": { "top": 20, "left": 20, "properties": {
		 * "title": "Question1", "inputs": { "input_0": { "label": "QuestionContent" }
		 * }, "outputs": { "output_0": { "label": "Answer1" } } } }, }, 
		 * "links": {
		 * "link_1": { "fromOperator": "operator1", "fromConnector": "output_0",
		 * "toOperator": "operator2", "toConnector": "input_0", } }, 
		 * "operatorTypes": {}
		 * }
		 */
		// operators 構築
		String operator = "";
		Integer questionCnt = 0;
		for(QuestionContentUpdateForm question : questionFormLst) {
			operator = operator + "    'operator"+ question.getId() + "': {\n";
			List<QuestionLinkForm> haveLinkQuestion  = linkFormLst.stream().filter(link -> link.getQuestionId().equals(question.getId())).toList();
			if(haveLinkQuestion == null || haveLinkQuestion.size() == 0 ) {
				  operator = operator + "      'top': 1100,\n";
				  operator = operator +"      'left': 300,\n";
			}else {
				  operator = operator + "      'top': 750+"+ ( 40*questionCnt ) +",\n";
				  operator = operator +"      'left': 300+" + ( +60*questionCnt ) + ",\n";
				  questionCnt ++;
			}
			
			operator = operator  + "      'properties': {\n";
			operator = operator  + "        'title': 'Question" +question.getQuestionOrderNo() +"',\n";
			operator = operator  + "        'inputs': {\n";
			operator = operator  + "          'input_"+ question.getId() +"': {\n";
			operator = operator  + "            'label': '"+ question.getQuestionTitle() + "'\n";
			operator = operator  + "          }\n";
			operator = operator  + "        },\n";
			operator = operator  + "        'outputs': {\n";
			// 回答設定
			String outputs = "";
			for(AnswerContentDto answer : question.getAnswerContentLst()) {
				outputs = outputs + "          'output_"+answer.getAnswerId() + "': {\n";
				outputs = outputs + "            'label': '"+answer.getAnswer() + "'\n";
				outputs = outputs + "          },\n";
			}
			outputs = outputs.substring(0, outputs.length() -2 ) + "\n";
			operator = operator  + outputs;
			operator = operator  + "        }\n";
			operator = operator  + "      }\n";
			operator = operator  + "    },\n";
		}
		
		// 評価結果をoperatorsに設定
		operator = operator + "    '"+ CommonConstants.FLOW_CHART_RESULT +"': {\n";
		operator = operator  + "      'top': 750,\n";
		operator = operator  + "      'left': 1500,\n";
		operator = operator  + "      'properties': {\n";
		operator = operator  + "        'title': '評価結果',\n";
		operator = operator  + "        'inputs': {\n";
		String inputs = "";
		for(CategoryContentDto result : category.getCategoryContentLst()) {
			inputs = inputs + "          'input_"+result.getId() + "': {\n";
			inputs = inputs + "            'label': '"+result.getSurveyResult() + "'\n";
			inputs = inputs + "          },\n";
		}
		inputs = inputs.substring(0, inputs.length() -2 ) + "\n";
		operator = operator  + inputs;
		operator = operator  + "        }\n";
		operator = operator  + "      }\n";
		operator = operator  + "    }\n";
		
		// links構築
		String link = "";
		for(QuestionLinkForm linkForm : linkFormLst) {
			link = link + "    'link_"+linkForm.getId() +"': {\n";
			link = link + "      'fromOperator': 'operator" +linkForm.getQuestionId() +"',\n";
			link = link + "      'fromConnector': 'output_"+ linkForm.getAnswerId() +"',\n";
			if(LinkType.NEXT_QUESTION.getCode().equals(linkForm.getLinkType()) ) {
				link = link + "      'toOperator': 'operator"+ linkForm.getLinkTo() +"',\n";
				link = link + "      'toConnector': 'input_"+ linkForm.getLinkTo()  +"'\n";
			}else if(LinkType.SURVEY_RESULT.getCode().equals(linkForm.getLinkType()) ) {
				link = link + "      'toOperator': 'result',\n";
				link = link + "      'toConnector': 'input_"+ linkForm.getLinkTo()  +"'\n";
			}
			link = link + "    },\n";
		}
		link = link.substring(0, link.length() -2 ) + "\n";
		
		String chartData = "";
		chartData = chartData + "{\n";
		chartData = chartData + "  'operators': {\n";
		chartData = chartData + operator;
		chartData = chartData + "  },\n";
		chartData = chartData + "  'links': {\n";
		chartData = chartData + link;
		chartData = chartData + "  },\n";
		chartData = chartData + "  'operatorTypes': {}\n";
		chartData = chartData + "}\n";

		return chartData;
	}
	
}

package jp.co.SurveyMaker.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.SurveyMaker.Repository.SurveyPattern.SurveyPatternRepository;
import jp.co.SurveyMaker.Service.Entity.SurveyPattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class SurveyPatternService {
	@Autowired
	private  SurveyPatternRepository surveyPatternRepository;
	
	// 診断コンテンツ検索
	public List<SurveyPattern> getAllPattern() throws Exception {
		return surveyPatternRepository.findByDeleteFlgFalse();
	}
}

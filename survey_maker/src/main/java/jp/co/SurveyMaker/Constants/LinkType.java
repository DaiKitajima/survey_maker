package jp.co.SurveyMaker.Constants;

import java.util.Optional;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LinkType implements CodedEnum {
	NEXT_QUESTION(			1, 		"次質問へ"),
	SURVEY_RESULT(			2, 		"評価結果へ");
	
	private final Integer code;
	
	@Getter
	private final String display;
	
	@Override
	public Integer getCode() {
		return code;
	}
	
	public static Optional<LinkType> of(Integer code){
		return Stream.of(LinkType.values())
				.filter(i -> i.getCode().equals(code))
				.findAny();
	}
}

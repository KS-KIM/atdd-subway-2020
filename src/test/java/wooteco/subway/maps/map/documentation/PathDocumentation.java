package wooteco.subway.maps.map.documentation;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.web.context.WebApplicationContext;

import wooteco.security.core.TokenResponse;
import wooteco.subway.common.documentation.Documentation;
import wooteco.subway.maps.map.application.MapService;
import wooteco.subway.maps.map.dto.PathResponse;
import wooteco.subway.maps.map.ui.MapController;
import wooteco.subway.maps.station.dto.StationResponse;

@WebMvcTest(controllers = {MapController.class})
public class PathDocumentation extends Documentation {
    protected TokenResponse tokenResponse;

    @Autowired
    private MapController mapController;

    @MockBean
    private MapService mapService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
            RestDocumentationContextProvider restDocumentationContextProvider) {
        super.setUp(webApplicationContext, restDocumentationContextProvider);
        tokenResponse = new TokenResponse("token");
    }

    @DisplayName("두 역의 최단 경로를 조회한다.")
    @Test
    void findPath() {
        PathResponse pathResponse = new PathResponse(
                Arrays.asList(new StationResponse(1L, "교대역", LocalDateTime.now(), LocalDateTime.now()),
                        new StationResponse(2L, "강남역", LocalDateTime.now(), LocalDateTime.now()),
                        new StationResponse(3L, "양재역", LocalDateTime.now(), LocalDateTime.now())),
                4, 3, 1_250);
        when(mapService.findPath(any(), any(), any())).thenReturn(pathResponse);

        given().log().all().
                header("Authorization", "Bearer " + tokenResponse.getAccessToken()).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                get("/paths?source=1&target=3&type=DURATION").
                then().
                log().all().
                apply(document("map/paths",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer auth credentials")
                        ),
                        requestParameters(
                                parameterWithName("source").description("출발 아이디"),
                                parameterWithName("target").description("도착 아이디"),
                                parameterWithName("type").description("조회 타입")
                        ),
                        responseFields(
                                fieldWithPath("stations").type(JsonFieldType.ARRAY).description("경로 목록"),
                                fieldWithPath("stations.[].id").type(JsonFieldType.NUMBER).description("지하철역 아이디"),
                                fieldWithPath("stations.[].name").type(JsonFieldType.STRING).description("지하철역 이름"),
                                fieldWithPath("stations.[].createdDate").type(JsonFieldType.STRING).description("생성일"),
                                fieldWithPath("stations.[].modifiedDate").type(JsonFieldType.STRING).description("변경일"),
                                fieldWithPath("duration").type(JsonFieldType.NUMBER).description("총 거리"),
                                fieldWithPath("distance").type(JsonFieldType.NUMBER).description("총 시간"),
                                fieldWithPath("fare").type(JsonFieldType.NUMBER).description("요금")))).
                extract();
    }
}

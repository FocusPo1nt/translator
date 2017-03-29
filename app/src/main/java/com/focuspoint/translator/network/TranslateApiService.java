package com.focuspoint.translator.network;



import com.focuspoint.translator.models.responseModels.LanguagesResponseModel;

import org.json.JSONObject;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by v_banko on 3/29/2017.
 */

public interface TranslateApiService {

    String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/";
    String API_KEY = "trnsl.1.1.20170328T194953Z.0ad1660ad3179057.72ee725632f8b67f86427be1dd70a17ff9567bed";
    String API_KEY_PARAM = QueryProperties.KEY + "=" + API_KEY;



    @GET("getLangs?" + API_KEY_PARAM)
    Observable<LanguagesResponseModel> getLangs(
            @Query(QueryProperties.UI) String language);
}

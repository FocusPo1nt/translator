package com.focuspoint.translator.network;

import com.focuspoint.translator.models.responseModels.LanguagesRM;
import com.focuspoint.translator.models.responseModels.TranslationRM;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface TranslateApiService {

    String BASE_URL_TRNSL = "https://translate.yandex.net/api/v1.5/tr.json/";
    String API_KEY_TRNSL = "trnsl.1.1.20170328T194953Z.0ad1660ad3179057.72ee725632f8b67f86427be1dd70a17ff9567bed";
    String API_KEY_TRNSL_REST = Values.KEY + "=" + API_KEY_TRNSL;





    @GET("getLangs?" + API_KEY_TRNSL_REST)
    Observable<LanguagesRM> getLangs(
            @Query(Values.UI) String language);

    @GET("translate?" + API_KEY_TRNSL_REST)
    Observable<TranslationRM> translate(
            @Query(Values.TEXT) String text,
            @Query(Values.LANG) String lang);
}

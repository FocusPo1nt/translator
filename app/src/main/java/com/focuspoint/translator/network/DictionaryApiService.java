package com.focuspoint.translator.network;

import com.focuspoint.translator.models.responseModels.DictionaryRM;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by root on 23.04.17.
 * да почему я узнал об этом в самый последния момент?;)
 */

public interface DictionaryApiService {

    String BASE_URL_DICT = "https://dictionary.yandex.net/api/v1/dicservice.json/";

    String API_KEY_DICT = "dict.1.1.20170423T092656Z.0a3300f0e334a463.83caf2eb9a76b9bc0d70f412d7dfba993ce174fd";
    String API_KEY_DICT_REST = Values.KEY + "=" + API_KEY_DICT;



    @GET("lookup?" + API_KEY_DICT_REST)
    Observable<DictionaryRM> lookup(
            @Query(Values.TEXT) String text,
            @Query(Values.LANG) String lang);
}

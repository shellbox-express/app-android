package com.example.augusto.shellbox_express.service;

import com.example.augusto.shellbox_express.model.Payload;
import com.example.augusto.shellbox_express.utils.Constants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by matheuscatossi on 5/15/17.
 */

public interface APIInterface {

    @POST(Constants.VOICE)
    Call<Payload> postVoice(@Body Payload payload);



}
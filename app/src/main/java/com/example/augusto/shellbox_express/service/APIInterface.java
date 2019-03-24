package com.example.augusto.shellbox_express.service;

import com.example.augusto.shellbox_express.model.Balance;
import com.example.augusto.shellbox_express.utils.Constants;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by matheuscatossi on 5/15/17.
 */

public interface APIInterface {

    @GET(Constants.GET_BALANCE)
    Call<Balance> getBalance();



}
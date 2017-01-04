package cz.petrkubes.ioweyou.Pojos;

import org.json.JSONObject;

import cz.petrkubes.ioweyou.Api.SimpleCallback;

/**
 * Created by petr on 4.1.17.
 */

public class ApiResult {

    public int code;
    public String message;
    public JSONObject json;
    public SimpleCallback callback;
    public boolean successfull;

}

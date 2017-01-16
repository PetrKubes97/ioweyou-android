package cz.petrkubes.ioweyou.Pojos;

import org.json.JSONObject;

import cz.petrkubes.ioweyou.Api.SimpleCallback;

/**
 * @author Petr Kubes
 */
public class ApiResult {

    public int code;
    public String message;
    public JSONObject json;
    public SimpleCallback callback;
    public boolean successfull;

}

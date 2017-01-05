package cz.petrkubes.ioweyou.Api;

/**
 * Created by petr on 2.11.16.
 */

public interface SimpleCallback {

    void onSuccess(int apiMethodCode);
    void onFailure(String message);
}


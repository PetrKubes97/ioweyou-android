package cz.petrkubes.payuback.Api;

/**
 * Created by petr on 2.11.16.
 */

public interface SimpleCallback {

    void onSuccess();
    void onFailure(String message);
}


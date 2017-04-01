package cz.petrkubes.ioweyou.Api;

/**
 * @author Petr Kubes
 */
public interface SimpleCallback {

    void onSuccess(int apiMethodCode);

    void onFailure(String message);
}


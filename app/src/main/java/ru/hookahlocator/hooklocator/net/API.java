package ru.hookahlocator.hooklocator.net;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class API {

    public final static String TOKEN = "?api_token=e0dovzp7xsc3x8ors509";
    public final static String URL = "http://hookahlocator.ru/";
    public final static String REQUEST_CITIES = "api/get_city_list/1" + TOKEN;
    public final static String REQUEST_PLACES = "main/ajax_get_zavs/%CITY_ABBR%";
    public final static String REQUEST_PLACE_FULL = "api/get_zav_by_id/%PLACE_ID%";
    public final static String REQUEST_PLACE_ADDITIONAL = "api//api/get_filter_hookah/%PLACE_ID%" + TOKEN;
}

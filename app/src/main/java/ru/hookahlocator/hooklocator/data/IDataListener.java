/**
 * Author:    Igor Vydrenkov
 * Created:   April 2013
 **/

package ru.hookahlocator.hooklocator.data;

import java.util.ArrayList;

import ru.hookahlocator.hooklocator.data.entities.BaseObject;

public interface IDataListener {
	public void onDataReady(ArrayList<? extends BaseObject> data);
	public void onDataFailure();
}

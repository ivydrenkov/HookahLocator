package ru.hookahlocator.hooklocator;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.startsWith;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Декабрь 2015
 */
public class SelectCityActivityTest extends ActivityInstrumentationTestCase2<SelectCityActivity> {

    private SelectCityActivity mActivity;


    public SelectCityActivityTest() {
        super(SelectCityActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
    }

    @SmallTest
    public void test() {
        onView(withId(R.id.sel_city_title))
                .check(matches(withText(containsString(mActivity.getString(R.string.sel_city_title)))));
        _testCitySelection();
    }

    private void _testCitySelection() {
        onData(hasToString(startsWith("Москва")))
                .inAdapterView(withId(R.id.sel_city_list)).atPosition(0)
                .perform(click());
    }
}

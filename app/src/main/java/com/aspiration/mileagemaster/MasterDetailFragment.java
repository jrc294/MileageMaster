package com.aspiration.mileagemaster;

import java.io.Serializable;
import java.util.Calendar;

public interface MasterDetailFragment extends TabFragment {

    public void reloadDetail(long id);

    public void updateCalendar(Calendar calendar);

    public void deleteItem();

}

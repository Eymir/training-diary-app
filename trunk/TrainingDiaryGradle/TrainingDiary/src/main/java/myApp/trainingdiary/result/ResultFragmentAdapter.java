package myApp.trainingdiary.result;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.List;

import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.utils.Const;

class ResultFragmentAdapter extends FragmentStatePagerAdapter {
    protected static final String[] CONTENT = new String[]{"This", "Is", "A", "Test",};
    private List<Exercise> exerciseListInTraining;


    public ResultFragmentAdapter(FragmentManager fm, long tr_id) {
        super(fm);
        exerciseListInTraining = DBHelper.getInstance(null).READ.getExerciseListInTraining(tr_id);
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(Const.LOG_TAG, "getItem at " + position);
        return ResultFragment.newInstance(exerciseListInTraining.get(position));
    }

    @Override
    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return exerciseListInTraining.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return exerciseListInTraining.get(position).getName();
    }


}
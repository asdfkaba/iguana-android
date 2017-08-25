package iguana.iguana.common;

import android.graphics.drawable.Icon;
import android.media.Image;
import android.widget.Spinner;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import iguana.iguana.R;
import iguana.iguana.models.Issue;

/**
 * Created by moritz on 18.08.17.
 */
public class CommonMethods {
    public CommonMethods(){}

    public Integer map_priority(String prioritiy_string) {
        switch (prioritiy_string) {
            case "Critical":
                return 4;
            case "High":
                return 3;
            case "Normal":
                return 2;
            case "Low":
                return 1;
            case "Unimportant":
                return 0;
        }
        return 2;
    }

    public String map_priority_to_string(int priority) {
        switch (priority) {
            case 4:
                return "Critical";
            case 3:
                return "High";
            case 2:
                return "Normal";
            case 1:
                return "Low";
            case 0:
                return "Unimportant";
        }
        return "Normal";
    }

    public int getPriorityImage(int priority) {
        switch (priority) {
            case 4:
                return R.drawable.priority_critical_24dp;
            case 3:
                return R.drawable.priority_high_24dp;
            case 2:
                return R.drawable.priority_normal_24dp;
            case 1:
                return R.drawable.priority_low_24dp;
            case 0:
                return R.drawable.priority_unimportant_24dp;
        }
        return R.drawable.priority_normal_24dp;
    }

    public void order_by(String order, List<Issue> issueList) {
        Comparator<Issue> comp;

        switch (order) {
            case "title":
                comp = new Comparator<Issue>() {
                    @Override
                    public int compare(Issue issue1, Issue issue2) {
                        return issue1.getTitle().toLowerCase().compareTo(issue2.getTitle().toLowerCase());
                    }
                };
                break;
            case "number":
                comp = new Comparator<Issue>() {
                    @Override
                    public int compare(Issue issue1, Issue issue2) {
                        return issue1.getNumber().compareTo(issue2.getNumber());
                    }
                };
                break;

            case "storypoints":
                comp = new Comparator<Issue>() {
                    @Override
                    public int compare(Issue issue2, Issue issue1) {
                        return issue1.getStorypoints().compareTo(issue2.getStorypoints());
                    }
                };
                break;

            case "priority":
                comp = new Comparator<Issue>() {
                    @Override
                    public int compare(Issue issue2, Issue issue1) {
                        return issue1.getPriority().compareTo(issue2.getPriority());
                    }
                };
                break;

            case "type":
                comp = new Comparator<Issue>() {
                    @Override
                    public int compare(Issue issue1, Issue issue2) {
                        return issue1.getType().compareTo(issue2.getType());
                    }
                };
                break;


            default:
                comp = new Comparator<Issue>() {
                    @Override
                    public int compare(Issue issue2, Issue issue1) {
                        return issue1.getNumber().compareTo(issue2.getNumber());
                    }
                };
                break;

        }
        Collections.sort(issueList, comp);
    }

    public int getIndex(Spinner spinner, String myString)
    {
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }


 }
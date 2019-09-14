package net.icebd.mdskrumi.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import net.icebd.mdskrumi.R;

import net.icebd.mdskrumi.ModelClass.Feedback;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedBackFragment extends Fragment {


    private Button back , submit;
    private EditText feedback , email;

    private DrawerLayout drawerLayout;


    public FeedBackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_feed_back, container, false);

        back = v.findViewById(R.id.navbackInFeedBack);
        submit = v.findViewById(R.id.feedBackSubmitButton);
        feedback = v.findViewById(R.id.feedBackET);
        email = v.findViewById(R.id.emailFeddBackET);

        drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = feedback.getText().toString();
                String s2 = email.getText().toString();
                if(s1.isEmpty()){
                    feedback.setError("This field is required");
                    return;
                }
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("iCompressor");
                ref.child("Feedback").push().setValue(new Feedback(s1,s2));
                goBack();
            }
        });



        return v;
    }

    private void goBack(){
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

}

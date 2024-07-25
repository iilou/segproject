package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentLogin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLogin extends Fragment {

    private NavController nav;
    private EditText editEmail;
    private EditText editPword;
    private Button confirm;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentLogin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentLogin.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentLogin newInstance(String param1, String param2) {
        FragmentLogin fragment = new FragmentLogin();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void loginRequest(String email, String password){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Get user auth
                    FirebaseUser user = auth.getCurrentUser();
                    if(user == null) return;
                    String uid = user.getUid();

                    // Read from Database
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                    myRef.child("users").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            // if unsuccessful move on
                            if(!task.isSuccessful()) return;

                            // read user data into data class object
                            DataSnapshot dataSnapshot = task.getResult();
                            User u = User.FromFirebaseData(dataSnapshot);
                            if(u == null) return;
                            u.setUID(uid);
                            MemUser.CURRENT_USER = u;

                            // navigate to dashboard
                            switch(u.getType()){
                                case("Client"):
                                    nav.navigate(R.id.action_fragmentLogin_to_fragmentDashboardClient);
                                    break;
                                case("Landlord"):
                                    nav.navigate(R.id.action_fragmentLogin_to_fragmentDashboardLandlord);
                                    break;
                                case("Property Manager"):
                                    nav.navigate(R.id.action_fragmentLogin_to_fragmentDashboardManager);
                                    break;
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_login, container, false);

        editEmail = v.findViewById(R.id.login_email_i);
        editPword = v.findViewById(R.id.login_pword_i);

        confirm = v.findViewById(R.id.login_confirm);

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        this.nav = Navigation.findNavController(view);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginRequest(editEmail.getText().toString(), editPword.getText().toString());
            }
        });

        view.findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginRequest("test00@gmail.com", "asdfjkl;");
            }
        });
        view.findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginRequest("test10@gmail.com", "asdfjkl;");
            }
        });
        view.findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginRequest("test20@gmail.com", "asdfjkl;");
            }
        });
    }
}



















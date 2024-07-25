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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSignup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSignup extends Fragment {

    FirebaseDatabase database;
    FirebaseAuth auth;

    NavController nav;

    Button selClient;
    Button selLandlord;
    Button selManager;

    EditText editFname;
    EditText editLname;
    EditText editEmail;
    EditText editPword;
    EditText editByear;
    EditText editAddr;

    private int type = 0;
    private final boolean[][] visibility = {{true,false}, {false,true}, {false,false}};


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentSignup() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSignup.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSignup newInstance(String param1, String param2) {
        FragmentSignup fragment = new FragmentSignup();
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

//    private boolean verifyString(boolean isNum){
//        return isNum?true:()
//    }

    private void userToDb(User u, String uid){
        DatabaseReference db = this.database.getReference();
        u.UserToFirebaseDb(db, uid);
    }

    private void signUpFirebase(User u){
        this.auth = FirebaseAuth.getInstance();
        this.auth.createUserWithEmailAndPassword(u.getEmailAddress(), u.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(requireContext(), "User Created", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = auth.getCurrentUser();
                    assert user != null;
                    userToDb(u, user.getUid());
                    MemUser.CURRENT_USER = u;
                    // MOVE TO DASHBOARD

                    switch(type){
                        case(0):
                            nav.navigate(R.id.action_fragmentSignup_to_fragmentDashboardClient);
                            break;
                        case(1):
                            nav.navigate(R.id.action_fragmentSignup_to_fragmentDashboardLandlord);
                            break;
                        case(2):
                            nav.navigate(R.id.action_fragmentSignup_to_fragmentDashboardManager);
                            break;
                    }
                    return;
                }
            }
        });
    }

    private void confirmSignUp(){
        String fname = this.editFname.getText().toString();
        String lname = this.editLname.getText().toString();
        String email = this.editEmail.getText().toString();
        String pword = this.editPword.getText().toString();
        String byear = this.editByear.getText().toString();
        String addr = this.editAddr.getText().toString();

        User u = User.UserBuilder(this.type,
                fname, lname, email, pword, byear, addr);
        if(u == null){
            Toast.makeText(requireContext(), "Auth Failed: Illegal Parameters",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        this.signUpFirebase(u);

        this.editFname.setText("Working");
        Toast.makeText(requireContext(), "Auth Success.",
                Toast.LENGTH_SHORT).show();

    }

    private void setSel(int type){
        this.type = type;
        this.editByear.setVisibility(this.visibility[this.type][0]?View.VISIBLE:View.INVISIBLE);
        this.editAddr.setVisibility(this.visibility[this.type][1]?View.VISIBLE:View.INVISIBLE);
    }

    /** type: 0 -> client 1 -> landlord 2 -> manager
     * */
    private void bindSel(Button b, int type){
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSel(type);
            }
        });
    }

//    private void impGhostText(EditText e, String ghostText){
//        e.setOnClickListener(new View.OnClickListener());
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.database = FirebaseDatabase.getInstance();

        View v = inflater.inflate(R.layout.fragment_signup, container, false);

        this.selClient = v.findViewById(R.id.signup_selc);
        this.selLandlord = v.findViewById(R.id.signup_sell);
        this.selManager = v.findViewById(R.id.signup_selm);

        this.editFname = v.findViewById(R.id.signup_fname_i);
        this.editLname = v.findViewById(R.id.signup_lname_i);
        this.editEmail = v.findViewById(R.id.signup_email_i);
        this.editPword = v.findViewById(R.id.signup_pword_i);
        this.editByear = v.findViewById(R.id.signup_byear_i);
        this.editAddr = v.findViewById(R.id.signup_addr_i);

        this.bindSel(this.selClient, 0);
        this.bindSel(this.selLandlord, 1);
        this.bindSel(this.selManager, 2);
        setSel(0);

        // Inflate the layout for this fragment
        return  v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Access NavController here
        nav = Navigation.findNavController(view);

        view.findViewById(R.id.signup_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmSignUp();
            }
        });
    }
}
package com.example.bookingluu.Customer;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.bookingluu.CustomerLoginPage;
import com.example.bookingluu.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class CustomerMainPage extends AppCompatActivity {

    ViewPagerFragmentAdapter viewPagerFragmentAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    private String[] titles= new String[]{"RESERVATION","MENU","RATING","DETAILS"};
    private Button addReviewBtn;
    private Dialog addRatingDialog;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId,currentUserName,currentUserImage;
    String numberOfRating;
    String currentRating;
    DocumentReference documentReference;
    DocumentReference documentReferenceToRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main_page);

        init();
        viewPagerFragmentAdapter= new ViewPagerFragmentAdapter(this);
        viewPager2.setAdapter(viewPagerFragmentAdapter);

        new TabLayoutMediator(tabLayout,viewPager2,((tab, position)->tab.setText(titles[position]))).attach();
        LinearLayout layout = ((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(0));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
        layoutParams.weight = 1.5f; // e.g. 0.5f
        layout.setLayoutParams(layoutParams);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==2){
                    addReviewBtn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                addReviewBtn.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRatingDialog = new Dialog(CustomerMainPage.this);
                addRatingDialog.setContentView(R.layout.add_rating_dialog);
                EditText commentText= addRatingDialog.findViewById(R.id.commentText);
                Button postBtn= addRatingDialog.findViewById(R.id.postBtn);
                Button cancelBtn= addRatingDialog.findViewById(R.id.cancelBtn);
                RatingBar ratingBar= addRatingDialog.findViewById(R.id.ratingBar);
                addRatingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                addRatingDialog.show();

                postBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String comment=commentText.getText().toString();
                        String rate = String.valueOf(ratingBar.getRating());
                        Rating rating = new Rating(currentUserName,comment,currentUserImage,rate);
                        DocumentReference documentReference =fStore.collection("restaurant").document("HollandFood").collection("RatingList").document(currentUserName+numberOfRating);
                        documentReference.set(rating);
                        updateRatingToFireStore(rate);
                        Toast.makeText(CustomerMainPage.this, "Rate Successful", Toast.LENGTH_SHORT).show();
                        addRatingDialog.dismiss();



                    }
                });


                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addRatingDialog.dismiss();
                    }
                });


            }
        });
    }

    public void init(){
        viewPager2=findViewById(R.id.viewPager);
        tabLayout=findViewById(R.id.tabLayout);
        addReviewBtn=findViewById(R.id.addReviewBtn);
        fStore=FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();
        userId=fAuth.getCurrentUser().getUid();
        documentReference= fStore.collection("customers").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                currentUserName= value.getString("fullName");
                currentUserImage=value.getString("image");
            }
        });

        documentReferenceToRating=fStore.collection("restaurant").document("HollandFood");
        documentReferenceToRating.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                numberOfRating= value.getString("numberOfRating");
                currentRating=value.getString("currentRating");
            }
        });
    }

    public void updateRatingToFireStore(String rate){
        double temp= Double.parseDouble(rate);
        double totalRate=Double.parseDouble(currentRating)*Integer.parseInt(numberOfRating)+temp;
        String res=String.valueOf(totalRate/(Integer.parseInt(numberOfRating)+1));
        documentReferenceToRating.update("numberOfRating",String.valueOf(Integer.parseInt(numberOfRating)+1));
        documentReferenceToRating.update("currentRating",res);

    }
}
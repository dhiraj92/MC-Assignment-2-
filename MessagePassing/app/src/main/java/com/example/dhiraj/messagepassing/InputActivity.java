package com.example.dhiraj.messagepassing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class InputActivity extends AppCompatActivity {
    String dbName = "test";
    String Name ="p1";
    String ID = "id1";
    String Age = "8";
    String Sex = "Male";
    EditText nameText;
    EditText ageText;
    EditText idText;
    RadioGroup sexSelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        Button nextButton = (Button) findViewById(R.id.buttonNext);
        Button clearButton = (Button) findViewById(R.id.buttonClear);
        nameText = (EditText)findViewById(R.id.patientName);
        ageText = (EditText)findViewById(R.id.age);
        idText = (EditText)findViewById(R.id.pid);
        sexSelect = (RadioGroup) findViewById(R.id.radioGroup);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idText.setText("");
                ageText.setText("");
                nameText.setText("");
                idText.requestFocus();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = nameText.getText().toString();
                ID = idText.getText().toString();
                Age = ageText.getText().toString();
                if(Name.equals("") || ID.equals("") || Age.equals("")){
                    Toast.makeText(InputActivity.this,"One or more required field(s) are empty",Toast.LENGTH_LONG).show();
                }
                else if(Name.contains(" ")){
                    Toast.makeText(InputActivity.this,"Enter name without space",Toast.LENGTH_LONG).show();
                }
                else{
                    Sex = ((RadioButton)findViewById(sexSelect.getCheckedRadioButtonId())).getText().toString();
                    dbName = Name + "_"+ID+"_"+Age+"_"+Sex;
                    Intent intent = new Intent(InputActivity.this, AccelerometerActivity.class).putExtra("paitentData",dbName);
                    startActivity(intent);
                }

            }
        });

    }
}

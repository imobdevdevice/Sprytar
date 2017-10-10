package com.sprytar.android.family;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.sprytar.android.R;
import com.sprytar.android.databinding.DialogAddMemberBinding;
import com.sprytar.android.family.DaggerAddMemberComponent;
import com.sprytar.android.data.model.FamilyMember;
import com.sprytar.android.presentation.BaseDialogFragment;
import com.sprytar.android.util.ui.SignUpEditText;

import org.joda.time.DateTime;
import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;


public class AddMemberDialog extends BaseDialogFragment implements AddMemberView, Validator.ValidationListener {

    public static final String FAMILY_MEMBER_PARAM = "com.sprytar.android.game.FamilyMemberParam";

    @SuppressWarnings("WeakerAccess")
    @Inject
    AddMemberPresenter presenter;

    private DialogAddMemberBinding binding;

    private AvatarAdapter adapter;

    private Drawable errorIcon;

    private Validator validator;

    @NotEmpty(message = "Missing birthday")
    private SignUpEditText birthdayEditText;

    @NotEmpty(message = "Missing nickname")
    private SignUpEditText nicknameEditText;

    private DatePickerDialog.OnDateSetListener dateFromListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            presenter.updateBirthdayData(year, monthOfYear + 1, dayOfMonth);
        }
    };

    public static AddMemberDialog newInstance(FamilyMember familyMember) {
        AddMemberDialog fragment = new AddMemberDialog();
        Bundle args = new Bundle();
        args.putParcelable(FAMILY_MEMBER_PARAM, Parcels.wrap(familyMember));
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        binding = DialogAddMemberBinding.inflate(LayoutInflater.from(getContext()), null, false);
        dialogBuilder.setView(binding.getRoot());

        createComponent().inject(this);
        presenter.attachView(this);

        TextView title = new TextView(this.getContext());
        title.setText(getActivity().getString(R.string.add_children_title));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryText));
        title.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorBackground));
        title.setTextSize(20);

        FamilyMember familyMember = Parcels.unwrap(getArguments().getParcelable(FAMILY_MEMBER_PARAM));
        presenter.setFamilyMember(familyMember);

        dialogBuilder.setCustomTitle(title);
        validator = new Validator(this);
        validator.setValidationListener(this);
        initUi();

        binding.postcodeTextView.setText(Html.fromHtml(getString(R.string.postcode)));

        return dialogBuilder.create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        presenter.onDestroyed();
        super.onDestroy();
    }

    private void initUi() {

        adapter = new AvatarAdapter(getActivity());

        binding.avatarGridview.setAdapter(adapter);
        binding.avatarGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.onAvatarClick(position);
            }
        });


        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        nicknameEditText = binding.nicknameEditText;
        birthdayEditText = binding.birthdayEditText;

        binding.birthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTime now = new DateTime();
                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        R.style.DialogTheme,
                        dateFromListener,
                        now.getYear(),
                        now.getMonthOfYear() - 1,
                        now.getDayOfMonth());
                dialog.show();
            }
        });

        errorIcon = ResourcesCompat.getDrawable(getResources(), R.drawable
                .ic_error_outline_red_24dp, null);

        errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());

    }

    private AddMemberComponent createComponent() {
        return DaggerAddMemberComponent
                .builder()
                .sessionComponent(getSessionComponent())
                .familyMemberModule(new FamilyMemberModule())
                .build();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateBirthday(String birthday) {
        binding.birthdayEditText.setText(birthday);
    }

    @Override
    public void closeDialog(boolean result) {
        FamilyMemberActivity callingActivity = (FamilyMemberActivity) getActivity();
        callingActivity.onAddMemberDialogResult(FamilyMemberActivity.RESULT_OK);
        dismiss();
    }

    @Override
    public void showAvatars(List<Uri> items, int selected) {
        adapter.setItems(items, selected);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showSelectedAvatar(int position) {
        adapter.setSelected(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showMemberInfo(String name, String birthday) {
        binding.nicknameEditText.setText(name);
        binding.birthdayEditText.setText(birthday);

        binding.addButton.setText(getContext().getString(R.string.update));
    }

    @Override
    public void onValidationSucceeded() {
        presenter.onAddButtonClick(binding.nicknameEditText.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getContext());

            // Display error messages ;)
            if (view instanceof SignUpEditText) {
                ((EditText) view).setError(message, errorIcon);
            } else {
                showError(message);
            }
        }
    }
}
package ru.adhocapp.instaprint.fragment;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.adhocapp.instaprint.R;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.RotationGestureDetector;

/**
 * Created by malugin on 09.04.14.
 */

public class CreatePostcardPageFragment extends Fragment {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    int pageNumber;

    public static RotationGestureDetector sRotationDetector;

    public static CreatePostcardPageFragment newInstance(int page) {
        CreatePostcardPageFragment createPostcardPageFragment = new CreatePostcardPageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        createPostcardPageFragment.setArguments(arguments);
        return createPostcardPageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.page_fragment_select_foto, null);

        switch (pageNumber) {
            case 0:
                view = inflater.inflate(R.layout.page_fragment_select_foto, null);
                final PhotoView v = (PhotoView) view.findViewById(R.id.ivUserFoto);
                sRotationDetector = new RotationGestureDetector(new RotationGestureDetector.OnRotationGestureListener() {
                    @Override
                    public void OnRotation(RotationGestureDetector rotationDetector) {
                        float angle = rotationDetector.getAngle();
                        if (CreatePostcardFragment.sSelectedImage != null) {
                            if (angle > 40) {
                                Matrix matrix = new Matrix();
                                matrix.postRotate(270);
                                CreatePostcardFragment.sSelectedImage = Bitmap.createBitmap(CreatePostcardFragment.sSelectedImage,
                                        0, 0, CreatePostcardFragment.sSelectedImage.getWidth(),
                                        CreatePostcardFragment.sSelectedImage.getHeight(), matrix, true);
                                v.setImageBitmap(CreatePostcardFragment.sSelectedImage);
                                sRotationDetector.resetAngle();
                            } else if (angle < -40) {
                                Matrix matrix = new Matrix();
                                matrix.postRotate(90);
                                CreatePostcardFragment.sSelectedImage = Bitmap.createBitmap(CreatePostcardFragment.sSelectedImage,
                                        0, 0, CreatePostcardFragment.sSelectedImage.getWidth(),
                                        CreatePostcardFragment.sSelectedImage.getHeight(), matrix, true);
                                v.setImageBitmap(CreatePostcardFragment.sSelectedImage);
                                sRotationDetector.resetAngle();
                            }
                        }
                        Log.e("RotationGestureDetector", "Rotation: " + Float.toString(angle));
                    }
                });
                break;
            case 1:
                view = inflater.inflate(R.layout.page_fragment_graphics, null);
                break;
            case 2:
                view = inflater.inflate(R.layout.page_fragment_edit_text, null);
                break;
            case 3: {
                view = inflater.inflate(R.layout.page_fragment_edit_address, null);
                View v_to = view.findViewById(R.id.address_to);
                TextView textView = (TextView) v_to.findViewById(R.id.contact_title);
                textView.setText(R.string.address_to_no_named);
                break;
            }
            case 4: {
                view = inflater.inflate(R.layout.page_fragment_preview, null);
                break;
            }
            case 5: {
                view = inflater.inflate(R.layout.page_fragment_result, null);
                break;
            }
        }

        return view;
    }
}

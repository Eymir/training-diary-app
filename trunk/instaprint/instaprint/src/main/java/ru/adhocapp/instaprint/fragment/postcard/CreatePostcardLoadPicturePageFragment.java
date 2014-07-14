package ru.adhocapp.instaprint.fragment.postcard;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.adhocapp.instaprint.R;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.RotationGestureDetector;

/**
 * Created by malugin on 09.04.14.
 */

public class CreatePostcardLoadPicturePageFragment extends Fragment {

    public static RotationGestureDetector sRotationDetector;

    public static CreatePostcardLoadPicturePageFragment newInstance() {
        CreatePostcardLoadPicturePageFragment createPostcardPageFragment = new CreatePostcardLoadPicturePageFragment();
        return createPostcardPageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final CreatePostcardMainFragment parent = (CreatePostcardMainFragment) getParentFragment();
        View view = inflater.inflate(R.layout.page_fragment_select_foto, null);
        final PhotoView v = (PhotoView) view.findViewById(R.id.ivUserFoto);
        if (parent.getOrder() != null && parent.getOrder().getRawFrontSidePath() != null)
            parent.selectRawPhoto(parent.getOrder().getRawFrontSidePath(), view);

        sRotationDetector = new RotationGestureDetector(new RotationGestureDetector.OnRotationGestureListener() {
            @Override
            public void OnRotation(RotationGestureDetector rotationDetector) {
                float angle = rotationDetector.getAngle();
                if (parent.sSelectedImage != null) {
                    if (angle > 40) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(270);
                        parent.sSelectedImage = Bitmap.createBitmap(parent.sSelectedImage,
                                0, 0, parent.sSelectedImage.getWidth(),
                                parent.sSelectedImage.getHeight(), matrix, true);
                        v.setImageBitmap(parent.sSelectedImage);
                        sRotationDetector.resetAngle();
                    } else if (angle < -40) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        parent.sSelectedImage = Bitmap.createBitmap(parent.sSelectedImage,
                                0, 0, parent.sSelectedImage.getWidth(),
                                parent.sSelectedImage.getHeight(), matrix, true);
                        v.setImageBitmap(parent.sSelectedImage);
                        sRotationDetector.resetAngle();
                    }
                }
                Log.d("RotationGestureDetector", "Rotation: " + Float.toString(angle));
            }
        });
        return view;
    }
}

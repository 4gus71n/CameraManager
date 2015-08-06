package com.astinx.cameramanager;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by agustin on 01/12/14.
 */
public class NewPublicationFragment extends Fragment {

    private static final String IMAGES = "images";
    private static final String TEXT = "text";
    private static final String CHECKBOX_ONLY_FRIENDS = "checkbox-friends";
    private static final int IMAGE_LIMIT = 10;
    private ArrayList<Media> images = new ArrayList<Media>();
    private EditText vShareText ;
    private View vRootView;
    private String mID = "" +  new Random().nextInt();

    private PublicationCameraManager mCameraManager;
    private CameraManager.CameraManagerReceiver mCameraManagerReceiver;
    private ImageView vCameraBtn, vGalleryBtn, vTrashCanBtn;
    private ImageViewAdapter mAdapter;
    private RelativeLayout vViewPagerContainer;
    private boolean isNewPost = true;
    private CirclePageIndicator vDotIndicator;
    private CustomDurationViewPager vViewPager;

    public boolean hasAnythingChange() {
        try {
            return (mAdapter.getCount() > 0 || (vShareText.getText().length() > 0));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(IMAGES)){
               images =  (ArrayList<Media>) savedInstanceState.getSerializable(IMAGES);
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_publication, container, false);
    }


    public void showDialogConfirmation(DialogInterface.OnClickListener yesCallback, DialogInterface.OnClickListener notCallback){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false)
        .setPositiveButton(getResources().getString(R.string.yes), yesCallback)
        .setNeutralButton(getResources().getString(R.string.discard), notCallback)
        .setTitle(getResources().getString(R.string.alert))
                .setMessage(getResources().getString(R.string.areyousurecancel));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void onPostClick() {
        if((vShareText.getText().length() == 0)){
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.textorpicrequired), Toast.LENGTH_SHORT).show();
        }else{
            int hasMultipleMedia = 0;
            if(isNewPost){
                if((mAdapter.getMediaImages().size() > 1))
                    hasMultipleMedia = 1;
            }

            if(isNewPost){
                int hasMedia = 0;
                if(mAdapter.getMediaImages().size() > 0)
                    hasMedia = 1;
                String availableToAll = "1";
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        vRootView = view;
        vViewPager = (CustomDurationViewPager) vRootView.findViewById(R.id.recycler_view_publication_images);
        mAdapter = new ImageViewAdapter();
        if(images.size() > 0)
            mAdapter.setImages(images);
        vViewPager.setAdapter(mAdapter);
        vShareText = (EditText) vRootView.findViewById(R.id.edit_text_share_text);
        vDotIndicator = (CirclePageIndicator) vRootView.findViewById(R.id.recycler_view_publication_images_dots);
        vDotIndicator.setViewPager(vViewPager);
        vCameraBtn = (ImageView) vRootView.findViewById(R.id.img_view_camera);
        vGalleryBtn = (ImageView) vRootView.findViewById(R.id.img_view_gallery);
        vTrashCanBtn = (ImageView) vRootView.findViewById(R.id.img_view_trash_can);
        vViewPagerContainer = (RelativeLayout) vRootView.findViewById(R.id.view_pager_container);


    }

    private void updateViewPagerVisibility() {
        if (mAdapter.getCount() <= 0) {
            vViewPagerContainer.setVisibility(View.GONE);
        } else {
            vViewPagerContainer.setVisibility(View.VISIBLE);
        }
    }

    private void veryNastyHackThatShouldBeRemovedAsSoonAsYouCan() {
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            final Handler handler = new Handler();
            //Everything works perfectly fine here, do not look this code and do not ask.
            @Override
            public void onChanged() {
                super.onChanged();
                vViewPager.setScrollDurationFactor(3d);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        vViewPager.setCurrentItem(mAdapter.getCount() - 1, true);
                        vViewPager.setScrollDurationFactor(1d);
                        handler.removeCallbacks(this);
                    }
                }, 500);
            }
        });
    }

    private void checkIfHasCamera() {
        if (!HardwareTools.hasCamera(getActivity())) {
            vCameraBtn.setVisibility(View.GONE);
        }
    }

    public class ImageViewAdapter extends PagerAdapter {
        private ArrayList<Media> images = new ArrayList<Media>();

        public ArrayList<Media> getMediaImages() {
            return images;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView page = new ImageView(getActivity());
            ViewPager.LayoutParams params =
                    new ViewPager.LayoutParams();
            params.width = ViewPager.LayoutParams.MATCH_PARENT;
            params.height = 200;
            page.setLayoutParams(params);
            page.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String path = images.get(position).getPath();
            ((ViewPager) container).addView(page);

            return page;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);

        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == ((ImageView)o);
        }

        public void setImages(ArrayList<Media> imageAdverts) {
            this.images = imageAdverts;
        }


        public void addImage(Media image) {
            this.images.add(image);
        }

        public void removeImage(int index) {
            if (images.size() > index && index > -1) {
                images.remove(index);
            } else {
                throw new RuntimeException("Programming error: ImageAdvert doesnt exist in the adapter.");
            }
        }
    }

    private void loadButtonsOnClick() {
        vCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter.getCount() < IMAGE_LIMIT) {
                    mCameraManager.getImageFromCamera(getActivity());
                } else {
                    Toast.makeText(getActivity(), getString(R.string.image_limit_reached), Toast.LENGTH_SHORT).show();
                }
            }
        });
        vGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter.getCount() < IMAGE_LIMIT) {
                    mCameraManager.getImageFromGallery(getActivity());
                } else {
                    Toast.makeText(getActivity(), getString(R.string.image_limit_reached), Toast.LENGTH_SHORT).show();
                }
            }
        });
        vTrashCanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = vViewPager.getCurrentItem();
                if (index >= 0) {
                    mAdapter.removeImage(index);
                }
                if (mAdapter.getCount() == 0) {
                    mAdapter.setImages(new ArrayList<Media>());
                }
                vViewPager.setAdapter(mAdapter);
                updateViewPagerVisibility();

            }
        });
    }

    private void configureCameraManager(Bundle savedInstanceState) {
        mCameraManager = new PublicationCameraManager(getResources().getInteger(R.integer.postWidth), getResources().getInteger(R.integer.postHeight), mID);
        mCameraManagerReceiver = new CameraManager.CameraManagerReceiver() {
            @Override
            protected void onResultOK(String aId, HashSet<String> aResults) {
                ArrayList<String> lst = new ArrayList<>();
                lst.addAll(aResults);
                for (String aResult : lst) {
                    mCameraManager.onProcessingFinished(aResult);
                }
                mAdapter.notifyDataSetChanged();
                updateViewPagerVisibility();
            }

            @Override
            protected void onResultError(String aId) {
            }
        };
        getActivity().registerReceiver(mCameraManagerReceiver, new IntentFilter(CameraManager.INTENT_ACTION));

        if (savedInstanceState != null) {
            mCameraManager.restoreInstance(savedInstanceState);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCameraManager.onCameraActivityResult(getActivity(), requestCode, resultCode, data);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle b;
        if (savedInstanceState != null) {
            b = savedInstanceState;
        } else {
            b = getArguments();
        }
        configureCameraManager(savedInstanceState);
        loadButtonsOnClick();
        checkIfHasCamera();
        veryNastyHackThatShouldBeRemovedAsSoonAsYouCan();
        updateViewPagerVisibility();
    }

    private void hidePhotosLayout() {
        vViewPagerContainer.setVisibility(View.GONE);
        LinearLayout cameraLayout = (LinearLayout) getView().findViewById(R.id.camera_container);
        cameraLayout.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(IMAGES, mAdapter.getMediaImages());
        mCameraManager.saveInstance(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        if (mCameraManagerReceiver != null) {
            getActivity().unregisterReceiver(mCameraManagerReceiver);
            mCameraManagerReceiver = null;
        }
        super.onDestroyView();
    }

    private class PublicationCameraManager extends CameraManager {

        protected PublicationCameraManager(int aWidth, int aHeight, String aID) {
            super(aWidth, aHeight, CameraManager.ADVERT_FOLDER, aID);
        }

        protected void onProcessingFinished(String result) {
            Media image = new Media();
            image.setPath(result);
            if (mAdapter.getCount() < IMAGE_LIMIT) {
                mAdapter.addImage(image);
            } else {
                Toast.makeText(getActivity(), getString(R.string.image_limit_reached), Toast.LENGTH_SHORT).show();
            }

        }
    }

}

package hcm.ditagis.com.tanhoa.qlsc.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.ArcGISFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import hcm.ditagis.com.tanhoa.qlsc.QuanLySuCo;
import hcm.ditagis.com.tanhoa.qlsc.R;
import hcm.ditagis.com.tanhoa.qlsc.entities.entitiesDB.KhachHangDangNhap;
import hcm.ditagis.com.tanhoa.qlsc.utities.Popup;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class SingleTapMapViewAsync extends AsyncTask<Point, FeatureLayer, Void> {
    private ProgressDialog mDialog;
    private MapView mMapView;
    private ArcGISFeature mSelectedArcGISFeature;
    private Popup mPopUp;
    private static double DELTA_MOVE_Y = 0;//7000;
    private android.graphics.Point mClickPoint;
    private boolean isFound = false;
    private Context mContext;

    public SingleTapMapViewAsync(Context context, Popup popup, android.graphics.Point clickPoint, MapView mapview) {
        this.mMapView = mapview;
        this.mPopUp = popup;
        this.mClickPoint = clickPoint;
        this.mContext = context;
        this.mDialog = new ProgressDialog(context, android.R.style.Theme_Material_Dialog_Alert);
    }

    @Override
    protected Void doInBackground(Point... points) {
        final ListenableFuture<List<IdentifyLayerResult>> listListenableFuture = mMapView.identifyLayersAsync(mClickPoint, 5, false, 1);
        listListenableFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                List<IdentifyLayerResult> identifyLayerResults = null;
                try {
                    identifyLayerResults = listListenableFuture.get();
                    for (IdentifyLayerResult identifyLayerResult : identifyLayerResults) {
                        {
                            List<GeoElement> elements = identifyLayerResult.getElements();
                            if (elements.size() > 0 && elements.get(0) instanceof ArcGISFeature && !isFound) {
                                isFound = true;
                                mSelectedArcGISFeature = (ArcGISFeature) elements.get(0);
                                long serviceLayerId = mSelectedArcGISFeature.getFeatureTable().
                                        getServiceLayerId();
                                if (KhachHangDangNhap.getInstance().getKhachHang().getGroupRole().equals(mContext.getString(R.string.group_role_thicong))) {
                                    if (serviceLayerId == ((ArcGISFeatureTable) QuanLySuCo.FeatureLayerDTGDiemSuCoThiCong.getLayer().getFeatureTable()).getServiceLayerId())
                                        publishProgress(QuanLySuCo.FeatureLayerDTGDiemSuCoThiCong.getLayer());
                                }
                                else
                                {
                                    if (serviceLayerId == ((ArcGISFeatureTable) QuanLySuCo.FeatureLayerDTGDiemSuCoGiamSat.getLayer().getFeatureTable()).getServiceLayerId())
                                        publishProgress(QuanLySuCo.FeatureLayerDTGDiemSuCoGiamSat.getLayer());
                                }
                            }
                        }
                    }
                    publishProgress(null);
                } catch (
                        InterruptedException e)

                {
                    e.printStackTrace();
                } catch (
                        ExecutionException e)

                {
                    e.printStackTrace();
                }
            }
        });
        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage("Đang xử lý...");
        mDialog.setCancelable(false);
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                publishProgress();
            }
        });
        mDialog.show();
    }

    @Override
    protected void onProgressUpdate(FeatureLayer... values) {
        super.onProgressUpdate(values);
        if (values != null && mSelectedArcGISFeature != null && values[0] != null) {

            FeatureLayer featureLayer = values[0];
            mPopUp.showPopup(mSelectedArcGISFeature, false);
        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}
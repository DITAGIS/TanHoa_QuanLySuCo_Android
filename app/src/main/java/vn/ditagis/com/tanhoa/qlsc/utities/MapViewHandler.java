package vn.ditagis.com.tanhoa.qlsc.utities;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import vn.ditagis.com.tanhoa.qlsc.R;
import vn.ditagis.com.tanhoa.qlsc.adapter.TraCuuAdapter;
import vn.ditagis.com.tanhoa.qlsc.async.SingleTapAddFeatureAsync;
import vn.ditagis.com.tanhoa.qlsc.async.SingleTapMapViewAsync;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;

/**
 * Created by ThanLe on 2/2/2018.
 */

public class MapViewHandler extends Activity {
    private static final int REQUEST_ID_IMAGE_CAPTURE = 1;
    private static double DELTA_MOVE_Y = 0;//7000;
    private FeatureLayer suCoTanHoaLayerThiCong;
    private Callout mCallout;
    private android.graphics.Point mClickPoint;
    private ArcGISFeature mSelectedArcGISFeature;
    private MapView mMapView;
    private boolean isClickBtnAdd = false;
    private ServiceFeatureTable mServiceFeatureTable;
    private Popup mPopUp;
    private Activity mActivity;
    private DApplication mApplication;

    public MapViewHandler(Callout callout, MapView mapView,
                          Popup popupInfos, Activity activity) {
        this.mActivity = activity;
        mApplication = (DApplication) activity.getApplication();
        this.mCallout = callout;
        this.mMapView = mapView;
        if (mApplication.getDFeatureLayer().getLayer() != null) {
            this.mServiceFeatureTable = (ServiceFeatureTable) mApplication.getDFeatureLayer().getLayer().getFeatureTable();
            this.suCoTanHoaLayerThiCong = mApplication.getDFeatureLayer().getLayer();
        }
        this.mPopUp = popupInfos;
//        this.isThiCong = KhachHangDangNhap.getInstance().getKhachHang().getGroupRole().equals(mActivity.getString(R.string.group_role_thicong));
    }


    public void setClickBtnAdd(boolean clickBtnAdd) {
        isClickBtnAdd = clickBtnAdd;
    }

    public void addFeature(Point pointFindLocation) {
        mClickPoint = mMapView.locationToScreen(pointFindLocation);

        SingleTapAddFeatureAsync singleTapAdddFeatureAsync = new SingleTapAddFeatureAsync(mActivity,
                mServiceFeatureTable, output -> {
            if (output != null) {
//                mPopUp.showPopup((ArcGISFeature) output, true);
            }
        });
        singleTapAdddFeatureAsync.execute();
    }

    public double[] onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Point center = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
        Geometry project = GeometryEngine.project(center, SpatialReferences.getWgs84());
        double[] location = {project.getExtent().getCenter().getX(), project.getExtent().getCenter().getY()};
        mClickPoint = new android.graphics.Point((int) e2.getX(), (int) e2.getY());
//        Geometry geometry = GeometryEngine.project(project, SpatialReferences.getWebMercator());
        return location;
    }

    public void onSingleTapMapView(MotionEvent e) {
        final Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
        mClickPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
        if (isClickBtnAdd) {
            mMapView.setViewpointCenterAsync(clickPoint, 10);
        } else {

            SingleTapMapViewAsync singleTapMapViewAsync = new SingleTapMapViewAsync(mActivity, mPopUp, mClickPoint, mMapView);
            singleTapMapViewAsync.execute(clickPoint);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getDateString() {
        SimpleDateFormat writeDate = new SimpleDateFormat("dd_MM_yyyy HH:mm:ss");
        writeDate.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
        return writeDate.format(Calendar.getInstance().getTime());
    }

    private String getTimeID() {
        return Constant.DATE_FORMAT.format(Calendar.getInstance().getTime());
    }

    public void queryByObjectID(int objectID) {
        final QueryParameters queryParameters = new QueryParameters();
        final String query = "OBJECTID = " + objectID;
        queryParameters.setWhereClause(query);
        final ListenableFuture<FeatureQueryResult> feature;
        feature = mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        feature.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = feature.get();
                    if (result.iterator().hasNext()) {
                        Feature item = result.iterator().next();
                        Envelope extent = item.getGeometry().getExtent();

                        mMapView.setViewpointGeometryAsync(extent);
                        suCoTanHoaLayerThiCong.selectFeature(item);
                        if (mApplication.getDFeatureLayer().getLayer() != null) {
                            mSelectedArcGISFeature = (ArcGISFeature) item;
                            if (mSelectedArcGISFeature != null)
                                mPopUp.showPopup(mSelectedArcGISFeature, false);
                        }
                    }

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void querySearch(String searchStr, final TraCuuAdapter adapter) {
        adapter.clear();
        adapter.notifyDataSetChanged();
        mCallout.dismiss();

        suCoTanHoaLayerThiCong.clearSelection();
        QueryParameters queryParameters = new QueryParameters();
        StringBuilder builder = new StringBuilder();
        builder.append("DiaChi  like N'%").append(searchStr).append("%'")
                .append(" or IDSuCo like '%").append(searchStr).append("%'");
        queryParameters.setWhereClause(builder.toString());
        final ListenableFuture<FeatureQueryResult> featureQueryResult = mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        featureQueryResult.addDoneListener(() -> {
            try {
                FeatureQueryResult result = featureQueryResult.get();
                Iterator iterator = result.iterator();
                while (iterator.hasNext()) {
                    Feature item = (Feature) iterator.next();
                    Map<String, Object> attributes = item.getAttributes();
                    String format_date = "";
                    String[] split = attributes.get(Constant.FIELD_SUCO.ID_SUCO).toString().split("_");
                    try {
                        format_date = Constant.DATE_FORMAT.format((new GregorianCalendar(Integer.parseInt(split[3]), Integer.parseInt(split[2]), Integer.parseInt(split[1])).getTime()));
                    } catch (Exception ignored) {

                    }
                    String viTri = "";
                    try {
                        viTri = attributes.get(Constant.FIELD_SUCO.DIA_CHI).toString();
                    } catch (Exception ignored) {

                    }
                    adapter.add(new TraCuuAdapter.Item(Integer.parseInt(attributes.get(mActivity.getString(R.string.Field_OBJECTID)).toString()),
                            attributes.get(Constant.FIELD_SUCO.ID_SUCO).toString(),
                            Integer.parseInt(attributes.get(Constant.FIELD_SUCO.TRANG_THAI).toString()), format_date, viTri));
                    adapter.notifyDataSetChanged();

//                        queryByObjectID(Integer.parseInt(attributes.get(Constant.OBJECTID).toString()));
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

    }
}


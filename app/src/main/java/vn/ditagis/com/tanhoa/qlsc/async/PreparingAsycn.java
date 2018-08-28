package vn.ditagis.com.tanhoa.qlsc.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import vn.ditagis.com.tanhoa.qlsc.R;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;
import vn.ditagis.com.tanhoa.qlsc.entities.DLayerInfo;
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB;
import vn.ditagis.com.tanhoa.qlsc.services.GetDMA;
import vn.ditagis.com.tanhoa.qlsc.services.GetVatTu;
import vn.ditagis.com.tanhoa.qlsc.utities.Preference;

public class PreparingAsycn extends AsyncTask<Void, Void, Void> {
    private ProgressDialog mDialog;
    private Activity mActivity;
    private AsyncResponse mDelegate;
    private DApplication mApplication;

    public interface AsyncResponse {
        void processFinish(Void output);
    }

    public PreparingAsycn(Activity activity, AsyncResponse delegate) {
        this.mActivity = activity;
        this.mDelegate = delegate;
        mApplication = (DApplication) activity.getApplication();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mDialog = new ProgressDialog(this.mActivity, android.R.style.Theme_Material_Dialog_Alert);
        this.mDialog.setMessage(mActivity.getApplicationContext().getString(R.string.preparing));
        this.mDialog.setCancelable(false);
        this.mDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            getLayerInfoAPI();
            new GetVatTu(mActivity.getApplicationContext()).getVatTuFromService();
            new GetDMA(mActivity.getApplicationContext()).getMaDMAFromService();
        } catch (Exception e) {
            Log.e("Lỗi lấy danh sách DMA", e.toString());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);


    }

    @Override
    protected void onPostExecute(Void value) {
//        if (khachHang != null) {
        mDialog.dismiss();
        this.mDelegate.processFinish(value);
//        }
    }

    private void getLayerInfoAPI() {
        try {
            String API_URL = mApplication.getConstant.LAYER_INFO;

            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setDoOutput(false);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", Preference.getInstance().loadPreference(mActivity.getApplication().getString(R.string.preference_login_api)));
                conn.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
                pajsonRouteeJSon(builder.toString());
            } catch (Exception e) {
                Log.e("error", e.toString());
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.e("Lỗi lấy LayerInfo", e.toString());
        }
    }

    private void pajsonRouteeJSon(String data) throws JSONException {
        if (data == null)
            return;
        String myData = "{ \"layerInfo\": ".concat(data).concat("}");
        JSONObject jsonData = new JSONObject(myData);
        JSONArray jsonRoutes = jsonData.getJSONArray("layerInfo");
        List<DLayerInfo> layerDTGS = new ArrayList<>();
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            String url = jsonRoute.getString(mActivity.getApplicationContext().getString(R.string.sql_coloumn_sys_url));
            if (url.startsWith("http://113.161.88.180:800/arcgis/rest/services/TanHoa/SuCo/FeatureServer")) {
                url = url.replace("TanHoa/SuCo", "TanHoa/THSuCo");
            } else if (url.startsWith("http://113.161.88.180:800/arcgis/rest/services/TanHoa/TanHoaSuCo/FeatureServer")) {
                url = url.replace("TanHoa/TanHoaSuCo", "TanHoa/THSuCo");

            }
            String definition = jsonRoute.getString(mActivity.getApplicationContext().getString(R.string.sql_column_sys_definition));
            if (definition.contains("null"))
                definition = null;
            String addFields = "";
            try {
                addFields = jsonRoute.getString(mActivity.getApplicationContext().getString(R.string.sql_column_sys_add_fields_arr));

            } catch (Exception ignored) {

            }
            layerDTGS.add(new DLayerInfo(jsonRoute.getString(mActivity.getApplicationContext().getString(R.string.sql_coloumn_sys_id)),
                    jsonRoute.getString(mActivity.getApplicationContext().getString(R.string.sql_coloumn_sys_title)),
                    url,
                    jsonRoute.getBoolean(mActivity.getString(R.string.sql_coloumn_sys_iscreate)), jsonRoute.getBoolean(mActivity.getApplicationContext().getString(R.string.sql_coloumn_sys_isdelete)),
                    jsonRoute.getBoolean(mActivity.getString(R.string.sql_coloumn_sys_isedit)), jsonRoute.getBoolean(mActivity.getApplicationContext().getString(R.string.sql_coloumn_sys_isview)),
                    definition,
                    jsonRoute.getString(mActivity.getApplicationContext().getString(R.string.sql_column_sys_out_fields_arr)),
                addFields,
                    jsonRoute.getString(mActivity.getApplicationContext().getString(R.string.sql_column_sys_update_fields_arr))));


        }
        ListObjectDB.getInstance().setLstFeatureLayerDTG(layerDTGS);

    }

}
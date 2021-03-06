package hotel.convenient.com.http;

import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

import hotel.convenient.com.base.BaseActivity;
import hotel.convenient.com.domain.Data;
import hotel.convenient.com.fragment.RecyclerViewFragment;
import hotel.convenient.com.utils.ToastUtil;

/**
 * 分页用的 回调接口   只在json特定的时候使用
 * Created by cwy on 2016/1/8 15:19
 */
public abstract class SimplePageCallback extends SimpleCallback {
    RecyclerViewFragment fragment;
   public SimplePageCallback(RecyclerViewFragment fragment) {
        super();
       this.fragment = fragment;
    }

    @Override
    public  void simpleSuccess(String url, String result, ResultJson resultJson) {
        fragment.setRefreshing(false);
        if (resultJson.getCode() == CODE_SUCCESS) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject data = jsonObject.getJSONObject("data");
            Data<JSONObject> pageData  = JSONObject.parseObject(data.toString(), Data.class);
            if(pageData.getCurrentPage()== BaseActivity.INIT_PAGE){
                firstPage(result,pageData.getCurrentPage(),pageData.getCountPage(),pageData.getList());
            }else {
                otherPages(result, pageData.getCurrentPage(), pageData.getCountPage(), pageData.getList());
            }
            if(pageData.getCurrentPage()+1>=pageData.getCountPage()){
                fragment.setIsFull(true);
                isFull(pageData.getCurrentPage(), pageData.getCountPage());
            }
        } else {
            ToastUtil.showShortToast(resultJson.getMsg());
        }
    }

    @Override
    public void simpleError(Request request, IOException ex) {
        super.simpleError(request, ex);
        fragment.setRefreshing(false);
    }

    /**
     * 第一页  一般重设list  若使用的是RecyclerViewFragment  可以调用 setList(list);
     * @param result   原始的json数据
     * @param currentPage  当前页
     * @param countPage   总页数
     * @param list   显示数据
     */
    public abstract void firstPage(String result,int currentPage, int countPage, List<JSONObject> list);

    /**
     * 第一页之后的页   一般在list之后添加数据  若使用的是RecyclerViewFragment  可以调用 addList(list);
     * @param result   原始的json数据
     * @param currentPage  当前页
     * @param countPage   总页数
     * @param list   显示数据
     */
    public abstract void otherPages(String result, int currentPage, int countPage, List<JSONObject> list);

    /**
     * 到最后一页   告诉recyclerView没有更多了  若使用的是RecyclerViewFragment  可以调用 isFull(true)
     * @param currentPage  当前页
     * @param countPage   总页数
     */
    public void isFull(int currentPage,int countPage){
        
    }
}

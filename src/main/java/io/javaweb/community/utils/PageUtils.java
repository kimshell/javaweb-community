package io.javaweb.community.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.javaweb.community.mybatis.domain.Order;
import io.javaweb.community.mybatis.domain.PageBounds;

/**
 * 分页工具
 * @author KevinBlandy
 *
 */
public class PageUtils {
	 /**
     * 默认页码
     */
    private static final Integer DEFAULT_PAGE = 1;

    /**
     * 默认每页显示数量
     */
    private static final Integer DEFAULT_ROWS = 10;

    public static PageBounds getPageBounds(Integer page, Integer rows, Order... orders){
        return PageUtils.getPageBounds(page,rows,true,orders);
    }
    
    public static PageBounds getPageBoundsNoneTotalCount(Integer page, Integer rows, Order... orders){
        return PageUtils.getPageBounds(page,rows,false,orders);
    }
    
    public static PageBounds getPageBounds(Integer page, Integer rows,Boolean totalCount, Order... orders){
        if(page == null || page < 1){
            page = DEFAULT_PAGE;
        }
        if(rows == null || rows < 1){
            rows = DEFAULT_ROWS;
        }
        PageBounds pageBounds = new PageBounds(page,rows);
        if(!GeneralUtils.isEmpty(orders)){
            pageBounds.setOrders(Arrays.asList(orders));
        }
        pageBounds.setContainsTotalCount(totalCount);
        pageBounds.setAsyncTotalCount(Boolean.FALSE);
        return pageBounds;
    }
   
    public static Order[] getOrders(String[] sorts,String[] orders){
        List<Order> result = new ArrayList<Order>();
        if(!GeneralUtils.isEmpty(sorts)){
            if(GeneralUtils.isEmpty(orders)){
                for(String sort : sorts){
                    if(!GeneralUtils.isEmpty(sort)){
                        result.add(getOrderDesc(sort));
                    }
                }
            }else{
                for(int x = 0 ;x < sorts.length ; x++){
                    if(GeneralUtils.isEmpty(sorts[x])){
                        continue;
                    }
                    if(x < orders.length){
                        if("ASC".equalsIgnoreCase(orders[x])){
                            result.add(getOrderAsc(sorts[x]));
                        }else{
                            result.add(getOrderDesc(sorts[x]));
                        }
                    }else{
                        result.add(getOrderDesc(sorts[x]));
                    }
                }
            }
        }
        return result.toArray(new Order[result.size()]);
    }

    public static Order getOrderDesc(String filed){
        return new Order(filed, Order.Direction.DESC,null);
    }

    public static Order getOrderAsc(String filed){
        return new Order(filed, Order.Direction.ASC,null);
    }
}

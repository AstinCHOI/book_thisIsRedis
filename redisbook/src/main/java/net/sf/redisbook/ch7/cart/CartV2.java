package net.sf.redisbook.ch7.cart;

import net.sf.redisbook.JedisHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.nio.channels.Pipe;
import java.util.List;
import java.util.Set;

public class CartV2 {
    private Jedis jedis;
    private JSONObject cartInfo;
    private String userNo;
    private static final String KEY_CART_LIST = ":cart:product";
    private static final String KEY_CART_PRODUCT = ":cart:productid:";
    private static final String JSON_PRODUCT_LIST = "products";
    private static final int EXPIRE = 60 * 60 * 24 * 3;

    public CartV2(JedisHelper helper, String userNo) {
        this.jedis = helper.getConnection();
        this.userNo = userNo;
        this.cartInfo = getCartInfo();
    }

    /**
     * 레디스에 저장된 장바구니 정보를 조회하여 JSON 객체로 변환한다.
     * @return 장바구니 정보가 저장된 JSONObject
     */
    private JSONObject getCartInfo() {
        String productInfo = this.jedis.get(this.userNo + KEY_CART_LIST);
        if (null == productInfo || "".equals(productInfo)) {
            return makeEmptyCart();
        }

        try {
            JSONParser parser = new JSONParser();
            return (JSONObject)parser.parse(productInfo);
        } catch (ParseException e) {
            return makeEmptyCart();
        }
    }

    /**
     * 장바구니가 존재하지 않는 사용자를 위한 빈 장바구니 정보를 생성한다.
     * @return 빈 장바구니 정보
     */
    public JSONObject makeEmptyCart() {
        JSONObject cart = new JSONObject();
        cart.put(JSON_PRODUCT_LIST, new JSONArray());
        return cart;
    }

    /**
     * 장바구니에 저장된 상품을 삭제한다.
     * @return 삭제된 상품개수
     */
    public int flushCart() {
        JSONArray products = (JSONArray)this.cartInfo.get(JSON_PRODUCT_LIST);

        Pipeline p = jedis.pipelined();
        for (int i = 0; i < products.size(); i++) {
            p.del(this.userNo + KEY_CART_PRODUCT + products.get(i));
        }

        p.set(this.userNo + KEY_CART_LIST, "");
        p.sync();

        return products.size();
    }

    /**
     * 장바구니에 상품을 추가한다.
     * @param productNo 장바구니에 추가할 상품번호
     * @param productName 장바구니에 추가할 상품명
     * @param quantity 상품개수
     * @return 장바구니 등록 결과
     */
    public String addProduct(String productNo, String productName, int quantity) {
        JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);
        products.add(productNo);

        this.jedis.set(this.userNo + KEY_CART_LIST, this.cartInfo.toJSONString());

        JSONObject product = new JSONObject();
        product.put("productNo", productNo);
        product.put("productName", productName);
        product.put("quantity", quantity);

        String productKey = this.userNo + KEY_CART_PRODUCT + productNo;
        return this.jedis.setex(productKey, EXPIRE, product.toJSONString());
    }

    /**
     * 장바구니에 저장된 상품 정보를 삭제한다.
     * @param productNo 삭제할 상품번호 목록
     * @return 삭제된 상품개수
     */
    public int deleteProduct(String[] productNo) {
        JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);
        int result = products.size();

        Pipeline p = jedis.pipelined();

        for (String item : productNo) {
            products.remove(item);
            p.del(this.userNo + KEY_CART_PRODUCT + item);
        }

        p.set(this.userNo + KEY_CART_LIST, this.cartInfo.toJSONString());
        p.sync();
        return result;
    }

    /**
     * 장바구니에 저장된 상품 목록을 JSONArray 형식으로 조회한다.
     * @return 조회된 상품 목록
     */
    public JSONArray getProductList() {
        boolean isChanged = false;
        JSONArray products = (JSONArray) this.cartInfo.get(JSON_PRODUCT_LIST);

        Pipeline p = jedis.pipelined();
        for (int i = 0; i < products.size(); i++) {
            p.get(this.userNo + KEY_CART_PRODUCT + products.get(i));

        }

        List<Object> redisResult = p.syncAndReturnAll();

        JSONArray result = new JSONArray();
        for(Object item : redisResult) {
            if (item == null) {
                isChanged = true;
            } else {
                result.add(item);
            }
        }

        if (isChanged) {
            this.jedis.set(this.userNo + KEY_CART_LIST, this.cartInfo.toJSONString());
        }

        return result;
    }

    /**
     * keys 명령을 사용하여 장바구니에 저장된 상품을 삭제한다.
     * @return 삭제된 상품개수
     * @deprecated keys 명령을 사용한 잘못된 구현이다.
     */
    public int flushCartDeprecated() {
        Set<String> keys = this.jedis.keys(this.userNo + KEY_CART_PRODUCT + "*");

        Pipeline p = jedis.pipelined();
        for (String key : keys) {
            this.jedis.del(key);
        }
        p.set(this.userNo + KEY_CART_LIST, "");
        p.sync();
        return keys.size();
    }
}

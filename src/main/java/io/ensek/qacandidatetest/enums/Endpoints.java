package io.ensek.qacandidatetest.enums;

/**
 * Created by Hiral Yagnik
 * Project Name: ENSEK_API
 */
//This class is used to store the endpoints of the API
public class Endpoints {
    public static final String Login = "/login";
    public static final String Reset = "/reset";
    public static final String GetEnergy = "/energy";
    public static final String BuyEnergy = "/buy/{id}/{quantity}";
    public static final String GetOrders = "/orders";
    public static final String GetOrderById = "/orders/{id}";

}

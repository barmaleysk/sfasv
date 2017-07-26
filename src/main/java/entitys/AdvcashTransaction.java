package entitys;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ac_transactions")
public class AdvcashTransaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String ac_src_wallet;// Текстовая строка Uxxxxxxxxxxxx, 12 символов кошелёк покупателя
    private String ac_dest_wallet;// Текстовая строка Uxxxxxxxxxxxx, 12 символов  кошелёк продавца
    @Column(scale = 2,precision = 10)
    private BigDecimal ac_amount;//00.00Сумма, начисленная на кошелек Продавца.
    @Column(scale = 2,precision = 10)
    private BigDecimal ac_merchant_amount;//Сумма, выставленная Покупателю.
    private String ac_merchant_currency;//ISO 4217 Валюта суммы, начисленной на кошелек Продавца.
    @Column(scale = 2,precision = 10)
    private BigDecimal ac_fee;//Комиссия, вычтеннаясистемой Advanced Cash со счета Покупателя при получении платежа.
    @Column(scale = 2,precision = 10)
    private BigDecimal ac_buyer_amount_without_commission;//Сумма платежа Покупателя без комиссии.
    @Column(scale = 2,precision = 10)
    private BigDecimal ac_buyer_amount_with_commission;//Сумма платежа Покупателя с учетом комиссии.
    private String ac_buyer_currency;//ISO 4217 Валюта, в которой Покупатель оплатил заказ.
    private String ac_transfer;//ID-номер операции Advanced Cash.
    private String ac_sci_name;//Название магазина Продавца.
    private LocalDateTime ac_start_date;//Дата операции. 2012-06-23 12:30:00
    private String ac_order_id;//идентифицирующий номер покупки userId+typeSubscribe+dateTime
    private String ac_ps;// Платежная система, которая использовалась. ADVANCED_CASH
    private String ac_transaction_status;//Статус транзакции. PENDING PROCESS CONFIRMED COMPLETED CANCELED
    private String ac_buyer_email;//E-mail Покупателя.
    private Boolean ac_buyer_verified;//Статус верификации Покупателя. true / false
    private String ac_comments;//Комментарий Продавца
    //private String Merchantcustomfield;//
    private String ac_hash;//HASH-строка, составленная из информации, содержащейся в данной форме, и секретного слова для защиты.

    @ManyToMany(fetch = FetchType.EAGER)
    private List<User> users;

     AdvcashTransaction() {
    }

    public AdvcashTransaction(String ac_src_wallet,
                              String ac_dest_wallet,
                              BigDecimal ac_amount,
                              BigDecimal ac_merchant_amount,
                              String ac_merchant_currency,
                              BigDecimal ac_fee,
                              BigDecimal ac_buyer_amount_without_commission,
                              BigDecimal ac_buyer_amount_with_commission,
                              String ac_buyer_currency,
                              String ac_transfer,
                              String ac_sci_name,
                              LocalDateTime ac_start_date,
                              String ac_order_id,
                              String ac_ps,
                              String ac_transaction_status,
                              String ac_buyer_email,
                              Boolean ac_buyer_verified,
                              String ac_comments,
                              String ac_hash,
                              User user) {
        this.ac_src_wallet = ac_src_wallet;
        this.ac_dest_wallet = ac_dest_wallet;
        this.ac_amount = ac_amount;
        this.ac_merchant_amount = ac_merchant_amount;
        this.ac_merchant_currency = ac_merchant_currency;
        this.ac_fee = ac_fee;
        this.ac_buyer_amount_without_commission = ac_buyer_amount_without_commission;
        this.ac_buyer_amount_with_commission = ac_buyer_amount_with_commission;
        this.ac_buyer_currency = ac_buyer_currency;
        this.ac_transfer = ac_transfer;
        this.ac_sci_name = ac_sci_name;
        this.ac_order_id = ac_order_id;
        this.ac_ps = ac_ps;
        this.ac_transaction_status = ac_transaction_status;
        this.ac_buyer_email = ac_buyer_email;
        this.ac_buyer_verified = ac_buyer_verified;
        this.ac_comments = ac_comments;
        this.ac_hash = ac_hash;
        this.users = new ArrayList<>();
        this.users.add(user);
    }

    public int getId() {
        return id;
    }

    public String getAc_src_wallet() {
        return ac_src_wallet;
    }

    public String getAc_dest_wallet() {
        return ac_dest_wallet;
    }

    public BigDecimal getAc_amount() {
        return ac_amount;
    }

    public BigDecimal getAc_merchant_amount() {
        return ac_merchant_amount;
    }

    public String getAc_merchant_currency() {
        return ac_merchant_currency;
    }

    public BigDecimal getAc_fee() {
        return ac_fee;
    }

    public BigDecimal getAc_buyer_amount_without_commission() {
        return ac_buyer_amount_without_commission;
    }

    public BigDecimal getAc_buyer_amount_with_commission() {
        return ac_buyer_amount_with_commission;
    }

    public String getAc_buyer_currency() {
        return ac_buyer_currency;
    }

    public String getAc_transfer() {
        return ac_transfer;
    }

    public String getAc_sci_name() {
        return ac_sci_name;
    }

    public LocalDateTime getAc_start_date() {
        return ac_start_date;
    }

    public String getAc_order_id() {
        return ac_order_id;
    }

    public String getAc_ps() {
        return ac_ps;
    }

    public String getAc_transaction_status() {
        return ac_transaction_status;
    }

    public String getAc_buyer_email() {
        return ac_buyer_email;
    }

    public Boolean getAc_buyer_verified() {
        return ac_buyer_verified;
    }

    public String getAc_comments() {
        return ac_comments;
    }

    public String getAc_hash() {
        return ac_hash;
    }

    public User getUser() {
        return users==null ? null : users.get(0);
    }

    @Override
    public String toString() {
        String s = "Id: "+id+"\n"
                +"кошелёк покупателя: "+ac_src_wallet+"\n"
                +"кошелёк продавца: "+ac_dest_wallet+"\n"
                +"начислено продавцу: "+ac_amount+"\n"
                +"выставленно покупателю: "+ac_merchant_amount+"\n"
                +"Вылюта начисления: "+ac_merchant_currency+"\n"
                +"комиссия с покупателя: "+ac_fee+"\n"
                +"платеж без комиссии: "+ ac_buyer_amount_without_commission+"\n"
                +"платеж с учетом комиссии: "+ac_buyer_amount_with_commission+"\n"
                +"валюта покупателя: "+ac_buyer_currency+"\n"
                +"id операции: "+ac_transfer+"\n"
                +"название магаза: "+ac_sci_name+"\n"
                +"дата: "+ac_start_date+"\n"
                +"id покупки: "+ac_order_id+"\n"
                +"платежная система: "+ac_ps+"\n"
                +"статус транзакции: "+ac_transaction_status+"\n"
                +"email покупателя: "+ac_buyer_email+"\n"
                +"статус верификации покупателя: "+ac_buyer_verified+"\n"
                +"комент продавца: "+ac_comments+"\n"
                +"ac hash: "+ac_hash;
        return s;
    }
}

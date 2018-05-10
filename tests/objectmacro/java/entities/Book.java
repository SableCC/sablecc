package entities;

@Entity
@Table(name="Book")
public class Book {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @NotNull
    @Column(name="id_book")
    private Integer id_book;
    
    
    
    @Column(name="A")
    private Integer A;
    
    
    @NotNull
    @Column(name="B")
    private String B;
    
    
    public void setid_book(Integer id_book){
        this.id_book = id_book;
    }
    
    public Integer setid_book(){
        return this.id_book;
    }
    
    public void setA(Integer A){
        this.A = A;
    }
    
    public Integer setA(){
        return this.A;
    }
    
    public void setB(String B){
        this.B = B;
    }
    
    public String setB(){
        return this.B;
    }
}
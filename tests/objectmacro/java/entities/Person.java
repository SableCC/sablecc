package entities;

@Entity
@Table(name="Person")
public class Person {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @NotNull
    @Column(name="id_person")
    private Integer id_person;
    
    
    
    @Column(name="A")
    private Integer A;
    
    
    @NotNull
    @Column(name="B")
    private String B;
    
    
    public void setid_person(Integer id_person){
        this.id_person = id_person;
    }
    
    public Integer setid_person(){
        return this.id_person;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class App {
    public static void main(String[] args) throws Exception {

        SellerDao sellerDao = DaoFactory.creatSellerDao();
        DepartmentDao departmentDao = DaoFactory.createDepartmentDao(); 

        System.out.println("=== Seller === ");

        System.out.println("\n # 1 Insert ");

        Seller newSeller = new Seller(null, "Joao", "joao@gmail.com", new Date(), 3000.0, new Department(3, null));
        sellerDao.insert(newSeller);
        System.out.println("> Inserted! New seller id: " + newSeller.getId());

        System.out.println("\n # 2 Update ");

        Seller upSeller = sellerDao.findById(8); 
        upSeller.setName("Maria");
        sellerDao.update(upSeller);
        System.out.println("> Update seller completed!");        

        System.out.println("\n # 3 deleteById");

        //sellerDao.deleteById(7);
        System.out.println("> Deleted!");

        System.out.println("\n # 4 findById");

        Seller seller = sellerDao.findById(3);
        System.out.println(seller);

        System.out.println("\n # 5 findAll");

        List<Seller> sellers = new ArrayList<>();
        sellers = sellerDao.findAll();
        for (Seller s : sellers) {
            System.out.println(s);
        }

        System.out.println("\n # 6 findSellersByDepartmentId");

        List<Seller> sellersByDepartment = sellerDao.findByDepartment(new Department(2, null));
        for (Seller s : sellersByDepartment) {
            System.out.println(s);
        }

        System.out.println("\n === Department === ");
        System.out.println("\n # 1 Insert ");

        Department newDepartment = new Department(null, "Novo Departamento");
        departmentDao.insert(newDepartment);
        System.out.println("> Interted! New deparment id: " + newDepartment.getId() );

        System.out.println("\n # 2 Update ");

        Department upDepartment = departmentDao.findById(6); 
        upDepartment.setName("Departamento Alterado");
        departmentDao.update(upDepartment);
        System.out.println("> Update deparment completed!");

        System.out.println("\n # 3 deleteById");

        //departmentDao.deleteById(5);
        System.out.println("> Deleted!");

        System.out.println("\n # 4 findById");

        Department department = departmentDao.findById(2);
        System.out.println(department);

        System.out.println("\n # 5 findAll");

        List<Department> departments = new ArrayList<>(); 
        departments = departmentDao.findAll();
        for (Department d : departments) {
            System.out.println(d);
        }
    }
}

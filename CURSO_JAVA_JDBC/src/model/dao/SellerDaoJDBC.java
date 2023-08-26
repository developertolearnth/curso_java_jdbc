package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

    /* Essa classe implementa os métodos declarados na interface SellerDao */

    private Connection conn;

    public SellerDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Seller obj) {
        PreparedStatement st = null; 
        try {
            st = conn.prepareStatement("INSERT INTO seller (Name, Email, BirthDate, BaseSalary, DepartmentId) VALUES ( ?, ?, ?, ?, ?)",
                                        Statement.RETURN_GENERATED_KEYS);

            st.setString(1, obj.getName());                                        
            st.setString(2, obj.getEmail());                                        
            st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
            st.setDouble(4, obj.getBaseSalary());
            st.setInt(5, obj.getDepartment().getId());

            int rowsAffected = st.executeUpdate();
            
            if ( rowsAffected > 0 ) {
                ResultSet rs = st.getGeneratedKeys(); 
                if ( rs.next() ){
                    int id = rs.getInt(1); 
                    obj.setId(id);
                }
                DB.closeResultSet(rs);
            } else {
                throw new DbException("Error! No rows affected!"); 
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage()); 
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(Seller obj) {
        PreparedStatement st = null; 
        try {
            st = conn.prepareStatement("UPDATE seller SET "
                                        + "Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
                                        + "WHERE Id = ?");

            st.setString(1, obj.getName());                                        
            st.setString(2, obj.getEmail());                                        
            st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
            st.setDouble(4, obj.getBaseSalary());
            st.setInt(5, obj.getDepartment().getId());
            st.setInt(6, obj.getId());

            st.executeUpdate();
            
        } catch (SQLException e) {
            throw new DbException(e.getMessage()); 
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null; 
        try {
            st = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");

            st.setInt(1, id);  

            st.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage()); 
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("SELECT s.*, d.name as DepName "
                                        + "FROM seller s "
                                        + "INNER JOIN department d "
                                        + "ON s.DepartmentId = d.Id "
                                        + "WHERE s.Id = ?");

            st.setInt(1, id);

            rs = st.executeQuery();

            if (rs.next()) {
                Department dep = instanciarDepartment(rs);
                Seller seller = instanciarSeller(rs, dep);
                return seller;
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }

        return null;
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("SELECT s.*, d.name as DepName "
                    + "FROM seller s "
                    + "INNER JOIN department d "
                    + "ON s.DepartmentId = d.Id "
                    + "ORDER BY s.Id");

            rs = st.executeQuery();

            List<Seller> list = new ArrayList<>();

            /* A ideia do map é para instanciar um objeto Department apenas uma vez */
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()) {
                Department dep = map.get(rs.getInt("DepartmentId"));

                if (dep == null) {
                    dep = instanciarDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }

                Seller seller = instanciarSeller(rs, dep);
                list.add(seller);
            }

            return list;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Seller> findByDepartment(Department department) {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("SELECT s.*, d.name as DepName "
                    + "FROM seller s "
                    + "INNER JOIN department d "
                    + "ON s.DepartmentId = d.Id "
                    + "WHERE s.DepartmentId = ? "
                    + "ORDER BY s.name");

            st.setInt(1, department.getId());

            rs = st.executeQuery();

            List<Seller> list = new ArrayList<>();

            /* A ideia do map é para instanciar um objeto Department apenas uma vez */
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()) {
                Department dep = map.get(rs.getInt("DepartmentId"));

                if (dep == null) {
                    dep = instanciarDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }

                Seller seller = instanciarSeller(rs, dep);
                list.add(seller);
            }

            return list;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    private Department instanciarDepartment(ResultSet rs) throws SQLException {
        Department obj = new Department();
        obj.setId(rs.getInt("DepartmentId"));
        obj.setName(rs.getString("DepName"));
        return obj;
    }

    private Seller instanciarSeller(ResultSet rs, Department dep) throws SQLException {
        Seller obj = new Seller();
        obj.setId(rs.getInt("Id"));
        obj.setName(rs.getString("Name"));
        obj.setEmail(rs.getString("Email"));
        obj.setBaseSalary(rs.getDouble("BaseSalary"));
        obj.setBirthDate(rs.getDate("BirthDate"));
        obj.setDepartment(dep);

        return obj;
    }

}

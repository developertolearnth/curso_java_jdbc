package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

    /* Essa classe implementa os métodos declarados na interface DepartmentDao */ 

    private Connection conn; 

    public DepartmentDaoJDBC(Connection conn){
        this.conn = conn; 
    }

    @Override
    public void insert(Department obj) {
        PreparedStatement st = null; 
        try {
            st = conn.prepareStatement("INSERT INTO department (Name) VALUES (?)",
                                        Statement.RETURN_GENERATED_KEYS);

            st.setString(1, obj.getName());  

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
    public void update(Department obj) {
        PreparedStatement st = null; 
        try {
            st = conn.prepareStatement("UPDATE department SET Name = ? WHERE Id = ?");

            st.setString(1, obj.getName());
            st.setInt(2, obj.getId());  

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
            st = conn.prepareStatement("DELETE FROM department WHERE Id = ?");

            st.setInt(1, id);  

            st.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage()); 
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null; 

        try {
            st = conn.prepareStatement("SELECT * FROM department WHERE Id = ?");

            st.setInt(1, id);
            
            rs = st.executeQuery();

            if( rs.next() ){
                return new Department(rs.getInt("Id"), rs.getString("Name")); 
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
    public List<Department> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("SELECT * FROM department ORDER BY name");

            rs = st.executeQuery();

            List<Department> list = new ArrayList<>();

            while (rs.next()) {
                list.add(new Department(rs.getInt("Id"), rs.getString("Name")));
            }

            return list;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }
}

package model.dao.impl;

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
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	private Connection conn;

//construtor com argumento
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

//implementaçao de cada um 
	@Override
	public void insert(Seller obj) {
		PreparedStatement st =  null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			//configurando os ???? esplace rolters
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment(). getId());
			
			//executando
			int rowsAffected = st.executeUpdate();
			//testando
			
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				//fechando o resultset
				DB.closeResultSet(rs);
			}
			else {//lançando execessao
				throw new DbException("Unexpected error ! No row affected! ");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st =  null;
		try {
			st = conn.prepareStatement(
					"UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ? ");
					
			//configurando os ???? esplace rolters
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment(). getId());
			st.setInt(6, obj.getId());
			
			//executando
			 st.executeUpdate();	
		//caso de excesao captura
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void deleteById(Integer id) {//comando para deletar 
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
			//configurando valor do splaceronte
			st.setInt(1, id);
			st.executeUpdate();
		}
		catch(SQLException e)
		{
			throw new DbException(e.getMessage());
		}
		finally {
			
			DB.closeStatement(st);
		}
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department  "
							+ "ON seller.DepartmentId = department.Id  " + "WHERE seller.Id = ?");
			// CONFIGURANDO ?
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				// testar se o vendedor e null e metodo pra retorna o vendedor pra id
				Department dep = instantiateDepartment(rs);
				Seller obj = instantiateSeller(rs, dep);
				return obj;

			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs); // nao fecha conexao
		}
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartment(dep);
		return obj;
	}

//criou o metodo
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
							"SELECT seller.*,department.Name as DepName "
							+ "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " 
							//retira apenas a linha da restrição"WHERE DepartmentId = ? " 
							+ "ORDER BY Name");
			// CONFIGURANDO ?
			//st.setInt(1, department.getId()); essa tbem apaga

			rs = st.executeQuery();
			// declarando uma lista
			List<Seller> list = new ArrayList<>();
			// declarando chave valor map
			Map<Integer, Department> map = new HashMap<>();

			while (rs.next()) {

				// antes de instaciar ve se existe
				Department dep = map.get(rs.getInt("DepartmentId"));

				// incluindo teste
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				} // com esse esquema o map .get pega ele se nao existir volta null

				// testar se o vendedor e null e metodo pra retorna o vendedor pra id

				Seller obj = instantiateSeller(rs, dep);
				list.add(obj);

			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs); // nao fecha conexao
		}
	}
	

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE DepartmentId = ? " + "ORDER BY Name");
			// CONFIGURANDO ?
			st.setInt(1, department.getId());

			rs = st.executeQuery();
			// declarando uma lista
			List<Seller> list = new ArrayList<>();
			// declarando chave valor map
			Map<Integer, Department> map = new HashMap<>();

			while (rs.next()) {

				// antes de instaciar ve se existe
				Department dep = map.get(rs.getInt("DepartmentId"));

				// incluindo teste
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				} // com esse esquema o map .get pega ele se nao existir volta null

				// testar se o vendedor e null e metodo pra retorna o vendedor pra id

				Seller obj = instantiateSeller(rs, dep);
				list.add(obj);

			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs); // nao fecha conexao
		}
	}

}

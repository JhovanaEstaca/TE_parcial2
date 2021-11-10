
package com.emergentes.controlador;

import com.emergentes.modelo.Seminario;
import com.emergentes.utiles.ConexionBD;
import java.io.IOException;
import java.util.logging.Level;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends HttpServlet {

  
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        //declarar sentencia
        PreparedStatement ps;
        ConexionBD canal= new ConexionBD();
        Connection conn=canal.conectar();
        ResultSet rs;
        String op;
        int id;
        
        ArrayList<Seminario> lista = new ArrayList<Seminario>();
        
        op=(request.getParameter("op")!=null)? request.getParameter("op"):"list";
        
        if(op.equals("list")){
            //Operacion para listar datos
            String sql= "select * from seminarios";
            try {
                ps=conn.prepareStatement(sql);
                rs=ps.executeQuery();
                
                while(rs.next()){
                    Seminario lib=new Seminario();
                    lib.setId(rs.getInt("id"));
                    lib.setTitulo(rs.getString("titulo"));
                    lib.setExpositor(rs.getString("expositor"));
                    lib.setFecha(rs.getString("fecha"));
                    lib.setHora(rs.getString("hora"));
                    lib.setCupo(rs.getInt("cupo"));
              
                    lista.add(lib);
                }
                
                request.setAttribute("lista", lista);
                request.getRequestDispatcher("index.jsp").forward(request, response);
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(op.equals("nuevo")){
            // Operacion para desplegar formulario
            Seminario li=new Seminario();
            //System.out.println(li.toString());
            request.setAttribute("lib", li);
            request.getRequestDispatcher("editar.jsp").forward(request, response);
        }
        if(op.equals("editar")){
            id=Integer.parseInt(request.getParameter("id"));
            try {
                //craer otro objeto
                Seminario lib1=new Seminario();
                
                ps=conn.prepareStatement("select * from seminarios where id=?");
                ps.setInt(1,id);
                rs=ps.executeQuery();
                
                if(rs.next()){
                    lib1.setId(rs.getInt("id"));
                    lib1.setTitulo(rs.getString("titulo"));
                    lib1.setExpositor(rs.getString("expositor"));
                    lib1.setFecha(rs.getString("fecha"));
                    lib1.setHora(rs.getString("hora"));
                    lib1.setCupo(rs.getInt("cupo"));
                }
                //Enviar al formulario
                request.setAttribute("lib", lib1);
                request.getRequestDispatcher("editar.jsp").forward(request, response);
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(op.equals("eliminar")){
            //Operacion para eliminar datos
            id=Integer.parseInt(request.getParameter("id"));
            try {
                ps=conn.prepareStatement("delete from seminarios where id=?");
                ps.setInt(1, id);
                ps.executeUpdate();
                response.sendRedirect("MainController");
                
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
              //decodificacion
        int id=Integer.parseInt(request.getParameter("id"));
        String titulo=request.getParameter("titulo");
        String expositor=request.getParameter("expositor");
        String fecha=request.getParameter("fecha");
        String hora=request.getParameter("hora");
        int cupo=Integer.parseInt(request.getParameter("cupo"));
        //encapsulado en un objeto
       Seminario lib=new Seminario();
        
        lib.setId(id);
        lib.setTitulo(titulo);
        lib.setExpositor(expositor);
        lib.setFecha(fecha);
        lib.setHora(hora);
        lib.setCupo(cupo);
        
        //conexion
        ConexionBD canal= new ConexionBD();
        Connection conn=canal.conectar();
        PreparedStatement ps;
        ResultSet rs;
        //Verificacion para la insercion
        if(id==0){
            //insertar registro
            String sql="insert into seminarios(titulo,expositor,fecha,hora,cupo) values(?,?,?,?,?)";
            
            try {
                ps=conn.prepareStatement(sql);
              
                ps.setString(1,lib.getTitulo());
                ps.setString(2,lib.getExpositor());
                ps.setString(3,lib.getFecha());
                ps.setString(4,lib.getHora());
                ps.setInt(5,lib.getCupo());
                
                ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }  
        }else{
            //actualizar registro
            String sql1="update seminarios set titulo=?,expositor=?,fecha=?,hora=?,cupo=? where id=?";
            try {
                ps=conn.prepareStatement(sql1);
                
                ps.setString(1,lib.getTitulo());
                ps.setString(2,lib.getExpositor());
                ps.setString(3,lib.getFecha());
                ps.setString(4,lib.getHora());
                ps.setInt(5,lib.getCupo());
                ps.setInt(6, lib.getId());
                //ejecutar la consulta
                ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        response.sendRedirect("MainController");
    }
}


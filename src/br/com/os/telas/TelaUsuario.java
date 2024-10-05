/*
 * The MIT License
 *
 * Copyright 2024 Antonio Gabriel Pinheiro da Silva.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package br.com.os.telas;

/**
 *
 * @author Antonio Gabriel Pinheiro da Silva
 */
import java.sql.*;
import br.com.os.dal.ModuloConexao;
import javax.swing.JOptionPane;

public class TelaUsuario extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form TelaUsuario
     */
    public TelaUsuario() {
        initComponents();
        conexao = ModuloConexao.connector();
    }

    private void consultar() {
        // Método para consultar usuarios (READ)
        String sql = "select * from tbusuarios where iduser=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, jTxtUserId.getText());
            rs = pst.executeQuery();
            if (rs.next()) {
                jTxtUserNome.setText(rs.getString(2));
                jTxtUserEmail.setText(rs.getString(3));
                jTxtUserLogin.setText(rs.getString(4));
                jTxtUserSenha.setText(rs.getString(5));
                //Consulta referente ao ComboBox Perfil
                jCboUserPerfil.setSelectedItem(rs.getString(6));
            } else {
                JOptionPane.showMessageDialog(null, "Usuário não cadastrado");
                // As linhas abaixo limpa os campos
                jTxtUserNome.setText(null);
                jTxtUserEmail.setText(null);
                jTxtUserLogin.setText(null);
                jTxtUserSenha.setText(null);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void adicionar() {
        // Método para adicionar usuarios (CREATE)
        String sql = "insert into tbusuarios(usuario,email,login,senha,perfil) values (?,?,?,?,?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, jTxtUserNome.getText());
            pst.setString(2, jTxtUserEmail.getText());
            pst.setString(3, jTxtUserLogin.getText());
            pst.setString(4, jTxtUserSenha.getText());
            pst.setString(5, jCboUserPerfil.getSelectedItem().toString());

            // Validação dos campos obrigatórios
            if (jTxtUserId.getText().isEmpty() || (jTxtUserNome.getText().isEmpty()
                    || (jTxtUserEmail.getText().isEmpty() || (jTxtUserLogin.getText().isEmpty()
                    || (jTxtUserSenha.getText().isEmpty()))))) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos Obrigatórios");
            } else {
                // A linha abaixo atualiza o conteúdo a tabela usuarios
                // O código abaixo é usada para confirmação de dados inseridos
                int adicionado = pst.executeUpdate();
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Usuário registrado com sucesso");
                    // Limpa campos após a inserção
                    jTxtUserNome.setText(null);
                    jTxtUserEmail.setText(null);
                    jTxtUserLogin.setText(null);
                    jTxtUserSenha.setText(null);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Método para alteração dos dados do usuário (UPDATE)
    private void alterar() {
        String sql = "update tbusuarios set usuario=?,email=?,login=?,senha=?,perfil=? where iduser=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, jTxtUserNome.getText());
            pst.setString(2, jTxtUserEmail.getText());
            pst.setString(3, jTxtUserLogin.getText());
            pst.setString(4, jTxtUserSenha.getText());
            pst.setString(5, jCboUserPerfil.getSelectedItem().toString());
            pst.setString(6, jTxtUserId.getText());
            if (jTxtUserId.getText().isEmpty() || (jTxtUserNome.getText().isEmpty()
                    || (jTxtUserEmail.getText().isEmpty() || (jTxtUserLogin.getText().isEmpty()
                    || (jTxtUserSenha.getText().isEmpty()))))) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos Obrigatórios");
            } else {
                // A linha abaixo atualiza o conteúdo a tabela usuarios
                // O código abaixo é usada para confirmação da alteração dos dados inseridos
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Dados do usuário alterados com sucesso");
                    // Limpa campos após a inserção
                    jTxtUserNome.setText(null);
                    jTxtUserEmail.setText(null);
                    jTxtUserLogin.setText(null);
                    jTxtUserSenha.setText(null);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Método responsável pela remoção dos usuários
    private void remover() {
        // A estrutura abaixo confirma a remoção do usuário
        int confirmacao = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este usuário?", "Atenção!", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            String sql = "delete from tbusuarios where iduser=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, jTxtUserId.getText());
                int apagado = pst.executeUpdate();
                if (apagado > 0);
                JOptionPane.showMessageDialog(null, "Usuário removido com sucesso");
                jTxtUserNome.setText(null);
                jTxtUserEmail.setText(null);
                jTxtUserLogin.setText(null);
                jTxtUserSenha.setText(null);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTxtUserId = new javax.swing.JTextField();
        jTxtUserNome = new javax.swing.JTextField();
        jTxtUserEmail = new javax.swing.JTextField();
        jTxtUserLogin = new javax.swing.JTextField();
        jTxtUserSenha = new javax.swing.JTextField();
        jCboUserPerfil = new javax.swing.JComboBox();
        jBtnUserCreate = new javax.swing.JButton();
        jBtnUserRead = new javax.swing.JButton();
        jBtnUserUpdate = new javax.swing.JButton();
        jBtnUserDelete = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Usuários");
        setPreferredSize(new java.awt.Dimension(720, 470));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Id");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("* Nome");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("* Email");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("* Login");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("* Senha");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("* Perfil");

        jTxtUserId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTxtUserIdActionPerformed(evt);
            }
        });

        jTxtUserEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTxtUserEmailActionPerformed(evt);
            }
        });

        jCboUserPerfil.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jCboUserPerfil.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Admin", "User" }));

        jBtnUserCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/os/icones/createIcone.png"))); // NOI18N
        jBtnUserCreate.setToolTipText("Adicionar");
        jBtnUserCreate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBtnUserCreate.setPreferredSize(new java.awt.Dimension(80, 80));
        jBtnUserCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnUserCreateActionPerformed(evt);
            }
        });

        jBtnUserRead.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/os/icones/readIcone.png"))); // NOI18N
        jBtnUserRead.setToolTipText("Pesquisar");
        jBtnUserRead.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBtnUserRead.setPreferredSize(new java.awt.Dimension(80, 80));
        jBtnUserRead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnUserReadActionPerformed(evt);
            }
        });

        jBtnUserUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/os/icones/updateIcone.png"))); // NOI18N
        jBtnUserUpdate.setToolTipText("Editar");
        jBtnUserUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBtnUserUpdate.setPreferredSize(new java.awt.Dimension(80, 80));
        jBtnUserUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnUserUpdateActionPerformed(evt);
            }
        });

        jBtnUserDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/os/icones/deleteIcone.png"))); // NOI18N
        jBtnUserDelete.setToolTipText("Deletar");
        jBtnUserDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBtnUserDelete.setPreferredSize(new java.awt.Dimension(80, 80));
        jBtnUserDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnUserDeleteActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("* Campos Obrigatórios");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jTxtUserId, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(130, 130, 130)
                .addComponent(jLabel7))
            .addGroup(layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jLabel2)
                .addGap(21, 21, 21)
                .addComponent(jTxtUserNome, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jLabel3)
                .addGap(26, 26, 26)
                .addComponent(jTxtUserEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jLabel4)
                .addGap(24, 24, 24)
                .addComponent(jTxtUserLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jLabel5)
                .addGap(19, 19, 19)
                .addComponent(jTxtUserSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jLabel6)
                .addGap(28, 28, 28)
                .addComponent(jCboUserPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jBtnUserCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73)
                .addComponent(jBtnUserRead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73)
                .addComponent(jBtnUserUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73)
                .addComponent(jBtnUserDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jTxtUserId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jTxtUserNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jTxtUserEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jTxtUserLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jTxtUserSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel6))
                    .addComponent(jCboUserPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBtnUserCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnUserRead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnUserUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnUserDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        setBounds(0, 0, 720, 470);
    }// </editor-fold>//GEN-END:initComponents

    private void jTxtUserEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTxtUserEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTxtUserEmailActionPerformed

    private void jBtnUserDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnUserDeleteActionPerformed
        // chama o método remover
        remover();
    }//GEN-LAST:event_jBtnUserDeleteActionPerformed

    private void jBtnUserReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnUserReadActionPerformed
        // chama o método consultar
        consultar();
    }//GEN-LAST:event_jBtnUserReadActionPerformed

    private void jBtnUserCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnUserCreateActionPerformed
        // Chama o método adicionar
        adicionar();
    }//GEN-LAST:event_jBtnUserCreateActionPerformed

    private void jBtnUserUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnUserUpdateActionPerformed
        // Chama o método alterar
        alterar();
    }//GEN-LAST:event_jBtnUserUpdateActionPerformed

    private void jTxtUserIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTxtUserIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTxtUserIdActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnUserCreate;
    private javax.swing.JButton jBtnUserDelete;
    private javax.swing.JButton jBtnUserRead;
    private javax.swing.JButton jBtnUserUpdate;
    private javax.swing.JComboBox jCboUserPerfil;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JTextField jTxtUserEmail;
    private javax.swing.JTextField jTxtUserId;
    private javax.swing.JTextField jTxtUserLogin;
    private javax.swing.JTextField jTxtUserNome;
    private javax.swing.JTextField jTxtUserSenha;
    // End of variables declaration//GEN-END:variables
}

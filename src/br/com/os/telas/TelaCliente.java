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

import java.sql.*;
import br.com.os.dal.ModuloConexao;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
// A linha abaixo importa recursos da biblioteca rs2xml.jar
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Antonio Gabriel Pinheiro da Silva
 */
public class TelaCliente extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form TelaCliente
     */
    public TelaCliente() {
        initComponents();
        conexao = ModuloConexao.connector();
    }

    private void adicionar() {
        // Método para adicionar clientes (CREATE)
        String sql = "insert into tbclientes(nome_cliente,email_cliente,telefone_cliente) values (?,?,?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, jTxtClienteNome.getText());
            pst.setString(2, jTxtClienteEmail.getText());
            pst.setString(3, jTxtClienteTelefone.getText());

            // Validação dos campos obrigatórios
            if ((jTxtClienteNome.getText().isEmpty()
                    || (jTxtClienteEmail.getText().isEmpty()))) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos Obrigatórios");
            } else {
                // A linha abaixo atualiza o conteúdo a tabela usuarios
                // O código abaixo é usada para confirmação de dados inseridos
                int adicionado = pst.executeUpdate();
                //System.out.println(adicionado);
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Cliente registrado com sucesso");
                    // Limpa campos após a inserção
                    limpar_campos();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Método para pesquisar clientes pelo nome com filtro (READ)
    private void pesquisar_cliente() {
        String sql = "select idcliente as id, nome_cliente as nome, email_cliente as email, telefone_cliente as telefone from tbclientes where nome_cliente like ?";
        try {
            pst = conexao.prepareStatement(sql);
            // Passando o conteúdo da caixa de pesquisa o ?
            // Atenção ao "%" - continuação da String sql

            pst.setString(1, jTxtClientePesquisar.getText() + "%");
            rs = pst.executeQuery();
            // A linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela
            jTblClientes.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Método para setar os campos do formulário com o conteúdo da tabela
    public void setar_campo() {
        int setar = jTblClientes.getSelectedRow();
        jTxtClienteId.setText(jTblClientes.getModel().getValueAt(setar, 0).toString());
        jTxtClienteNome.setText(jTblClientes.getModel().getValueAt(setar, 1).toString());
        jTxtClienteEmail.setText(jTblClientes.getModel().getValueAt(setar, 2).toString());
        jTxtClienteTelefone.setText(jTblClientes.getModel().getValueAt(setar, 3).toString());

        // A linha abaixo desabilita o botão adicionar
        jBtnClienteCreate.setEnabled(false);
    }

    // Método para alteração dos dados do cliente (UPDATE)
    private void alterar() {
        String sql = "update tbclientes set nome_cliente=?,email_cliente=?,telefone_cliente=? where idcliente=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, jTxtClienteNome.getText());
            pst.setString(2, jTxtClienteEmail.getText());
            pst.setString(3, jTxtClienteTelefone.getText());
            // Pega o IdCli da grid aqui.
            int setar = jTblClientes.getSelectedRow();
            pst.setString(4, (jTblClientes.getModel().getValueAt(setar, 0).toString()));
            if ((jTxtClienteNome.getText().isEmpty()
                    || (jTxtClienteEmail.getText().isEmpty()))) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos Obrigatórios");
            } else {
                // A linha abaixo atualiza o conteúdo a tabela clientes
                // O código abaixo é usada para confirmação da alteração dos dados inseridos
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Dados do cliente alterados com sucesso");
                    // Limpa campos após a inserção
                    limpar_campos();
                    jBtnClienteCreate.setEnabled(true);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Método responsável pela remoção do cliente
    private void remover() {
        // A estrutura abaixo confirma a remoção do usuário
        int confirmacao = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este cliente?", "Atenção!", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            String sql = "delete from tbclientes where idcliente=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, jTxtClienteId.getText());
                int apagado = pst.executeUpdate();
                if (apagado > 0);
                JOptionPane.showMessageDialog(null, "Cliente removido com sucesso");
                limpar_campos();
                jBtnClienteCreate.setEnabled(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    // Esse método limpa os campos do formulário
    private void limpar_campos() {
        jTxtClientePesquisar.setText(null);
        jTxtClienteId.setText(null);
        jTxtClienteNome.setText(null);
        jTxtClienteEmail.setText(null);
        jTxtClienteTelefone.setText(null);
        ((DefaultTableModel) jTblClientes.getModel()).setRowCount(0);
        // O botão é reabilitado após a limpeza dos campos
        jBtnClienteCreate.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTxtClienteTelefone = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTxtClienteNome = new javax.swing.JTextField();
        jTxtClienteEmail = new javax.swing.JTextField();
        jBtnClienteCreate = new javax.swing.JButton();
        jBtnClienteUpdate = new javax.swing.JButton();
        jBtnClienteDelete = new javax.swing.JButton();
        jTxtClientePesquisar = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTblClientes = new javax.swing.JTable();
        jBtnLimparCampo = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jTxtClienteId = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Clientes");
        setPreferredSize(new java.awt.Dimension(720, 470));

        jTxtClienteTelefone.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("* Campos Obrigatórios");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("* Nome");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("* Email");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Telefone");

        jTxtClienteNome.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jTxtClienteEmail.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTxtClienteEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTxtClienteEmailActionPerformed(evt);
            }
        });

        jBtnClienteCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/os/icones/createIcone.png"))); // NOI18N
        jBtnClienteCreate.setToolTipText("Adicionar");
        jBtnClienteCreate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBtnClienteCreate.setPreferredSize(new java.awt.Dimension(80, 80));
        jBtnClienteCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnClienteCreateActionPerformed(evt);
            }
        });

        jBtnClienteUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/os/icones/updateIcone.png"))); // NOI18N
        jBtnClienteUpdate.setToolTipText("Editar");
        jBtnClienteUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBtnClienteUpdate.setPreferredSize(new java.awt.Dimension(80, 80));
        jBtnClienteUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnClienteUpdateActionPerformed(evt);
            }
        });

        jBtnClienteDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/os/icones/deleteIcone.png"))); // NOI18N
        jBtnClienteDelete.setToolTipText("Deletar");
        jBtnClienteDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBtnClienteDelete.setPreferredSize(new java.awt.Dimension(80, 80));
        jBtnClienteDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnClienteDeleteActionPerformed(evt);
            }
        });

        jTxtClientePesquisar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTxtClientePesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTxtClientePesquisarKeyReleased(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/os/icones/readIcone32px.png"))); // NOI18N

        jTblClientes = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        jTblClientes.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Nome", "Email", "Telefone"
            }
        ));
        jTblClientes.setFocusable(false);
        jTblClientes.getTableHeader().setReorderingAllowed(false);
        jTblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTblClientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTblClientes);

        jBtnLimparCampo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jBtnLimparCampo.setText("Limpar Campos");
        jBtnLimparCampo.setToolTipText("Habilita o botão Adicionar");
        jBtnLimparCampo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLimparCampoActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Id Cliente");

        jTxtClienteId.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTxtClienteId.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(105, 105, 105)
                        .addComponent(jTxtClientePesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(83, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 603, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel3))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTxtClienteEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTxtClienteTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTxtClienteNome, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jBtnClienteCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(69, 69, 69)
                                                .addComponent(jBtnClienteUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(69, 69, 69)
                                                .addComponent(jBtnClienteDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addComponent(jLabel6)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(69, 69, 69)
                                        .addComponent(jTxtClienteId, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jBtnLimparCampo))))))
                .addGap(18, 18, 18))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTxtClientePesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel7)
                        .addGap(23, 23, 23)
                        .addComponent(jBtnLimparCampo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTxtClienteId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTxtClienteNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTxtClienteEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jTxtClienteTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(33, 33, 33)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBtnClienteCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnClienteUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnClienteDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        setBounds(0, 0, 720, 470);
    }// </editor-fold>//GEN-END:initComponents

    private void jTxtClienteEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTxtClienteEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTxtClienteEmailActionPerformed

    private void jBtnClienteCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnClienteCreateActionPerformed
        // Chama o método adicionar
        adicionar();
    }//GEN-LAST:event_jBtnClienteCreateActionPerformed

    private void jBtnClienteUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnClienteUpdateActionPerformed
        // Chama o método alterar
        alterar();
    }//GEN-LAST:event_jBtnClienteUpdateActionPerformed

    private void jBtnClienteDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnClienteDeleteActionPerformed
        // chama o método remover
        remover();
    }//GEN-LAST:event_jBtnClienteDeleteActionPerformed

    private void jTxtClientePesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxtClientePesquisarKeyReleased
        // O evento acima é do tipo "enquanto digita"
        pesquisar_cliente();
    }//GEN-LAST:event_jTxtClientePesquisarKeyReleased

    private void jTblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTblClientesMouseClicked
        // O evento acima será usado para setar os campos da tabela (clicando com o mouse)
        setar_campo();
    }//GEN-LAST:event_jTblClientesMouseClicked

    private void jBtnLimparCampoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLimparCampoActionPerformed
        // Chama o método limpar_campos
        limpar_campos();
    }//GEN-LAST:event_jBtnLimparCampoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnClienteCreate;
    private javax.swing.JButton jBtnClienteDelete;
    private javax.swing.JButton jBtnClienteUpdate;
    private javax.swing.JButton jBtnLimparCampo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTblClientes;
    private javax.swing.JTextField jTxtClienteEmail;
    private javax.swing.JTextField jTxtClienteId;
    private javax.swing.JTextField jTxtClienteNome;
    private javax.swing.JTextField jTxtClientePesquisar;
    private javax.swing.JTextField jTxtClienteTelefone;
    // End of variables declaration//GEN-END:variables
}

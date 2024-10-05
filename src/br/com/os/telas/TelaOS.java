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
import com.mysql.cj.xdevapi.Result;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Antonio Gabriel Pinheiro da Silva
 */
public class TelaOS extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    // A linha abaixo cria uma variável para armazenar um texto de acordo com o
    // radio button
    private String tipo;

    /**
     * Creates new form TelaOS
     */
    public TelaOS() {
        initComponents();
        conexao = ModuloConexao.connector();
    }

    private void pesquisar_cliente() {
        String sql = "select idcliente as Id, nome_cliente as Nome, telefone_cliente as Telefone from tbclientes where nome_cliente like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, jTxtClientePesquisar.getText() + "%");
            rs = pst.executeQuery();
            jTblClientes.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void setar_campos() {
        int setar = jTblClientes.getSelectedRow();
        jTxtClienteId.setText(jTblClientes.getModel().getValueAt(setar, 0).toString());
    }

    // Método para cadastrar uma OS
    private void cadastrar_os() {
        String sql = "insert into tbos(tipo,status_equipamento,nome_equipamento,marca,modelo,numero_serie,descricao_problema,descricao_reparo,nome_tecnico,valor,idcliente) values(?,?,?,?,?,?,?,?,?,?,?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, tipo);
            pst.setString(2, jCboOsSituacao.getSelectedItem().toString());
            pst.setString(3, jTxtOsEquipamento.getText());
            pst.setString(4, jTxtOsMarca.getText());
            pst.setString(5, jTxtOsModelo.getText());
            pst.setString(6, jTxtOsNumeroSerie.getText());
            pst.setString(7, jTxtOsDefeito.getText());
            pst.setString(8, jTxtOsReparo.getText());
            pst.setString(9, jTxtOsTecnico.getText());
            pst.setString(10, jTxtOsValor.getText().replace(",", ".")); //Substitui a vírgula pelo Ponto
            pst.setString(11, jTxtClienteId.getText());
            // Validação dos campos obrigatórios
            if (jTxtClienteId.getText().isEmpty() || (jTxtOsEquipamento).getText().isEmpty()
                    || (jTxtOsTecnico).getText().isEmpty()
                    || (jCboOsSituacao).getSelectedItem().equals(" ")) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0);
                JOptionPane.showMessageDialog(null, "OS cadastrada com sucesso!");
                // Recuperar o id da Os
                recuperar_os();
                jBtnOsCreate.setEnabled(false);
                jBtnOsRead.setEnabled(false);
                jBtnOsImprimir.setEnabled(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Método para pesquisar OS
    private void pesquisar_os() {
        // A linha abaixo cria uma caixa de entrada do tipo JOptionPane
        String numero_os = JOptionPane.showInputDialog("Número da Os");
        // Modifique a query para buscar também o nome do cliente
        String sql = "SELECT tbos.*, tbclientes.nome_cliente "
                + "FROM tbos "
                + "JOIN tbclientes ON tbos.idcliente = tbclientes.idcliente "
                + "WHERE os = " + numero_os;
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                jTxtOs.setText(rs.getString(1));

                // Convertendo e formatando a data para o padrão brasileiro
                String dataOs = rs.getString(2); // Pegando a data do banco
                SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Padrão esperado do banco
                SimpleDateFormat formatoSaida = new SimpleDateFormat("dd/MM/yyyy - HH:mm"); // Padrão brasileiro
                Date dataFormatada = formatoEntrada.parse(dataOs); // Convertendo para objeto Date
                jTxtData.setText(formatoSaida.format(dataFormatada)); // Formatando e exibindo a data

                // Setando os radiobuttons
                String jRbtTipo = rs.getString(3);
                if (jRbtTipo.equals("OS")) {
                    jRbtnOs.setSelected(true);
                    tipo = "OS";
                } else {
                    jRbtnOrcamento.setSelected(true);
                    tipo = "Orçamento";
                }
                jCboOsSituacao.setSelectedItem(rs.getString(4));
                jTxtOsEquipamento.setText(rs.getString(5));
                jTxtOsMarca.setText(rs.getString(6));
                jTxtOsModelo.setText(rs.getString(7));
                jTxtOsNumeroSerie.setText(rs.getString(8));
                jTxtOsDefeito.setText(rs.getString(9));
                jTxtOsReparo.setText(rs.getString(10));
                jTxtOsTecnico.setText(rs.getString(11));
                jTxtOsValor.setText(rs.getString(12));
                jTxtClienteId.setText(rs.getString(13));

                // Preenchendo o campo do nome do cliente a partir do resultado do JOIN
                jTxtClientePesquisar.setText(rs.getString("nome_cliente"));

                // Desabilita o botão de adicionar e pesquisar
                jBtnOsCreate.setEnabled(false);
                jBtnOsRead.setEnabled(false);
                // Ativar demais botões
                jBtnOsUpdate.setEnabled(true);
                jBtnOsDelete.setEnabled(true);
                jBtnOsImprimir.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(null, "Os não cadastrada");
            }
        } catch (java.sql.SQLSyntaxErrorException e) {
            JOptionPane.showMessageDialog(null, "Os Inválida (Letras não são permitidas)");
        } catch (Exception e2) {
            JOptionPane.showMessageDialog(null, e2);
        }
    }

    // Método para alterar uma OS
    private void alterar_os() {
        String sql = "update tbos set tipo=?,status_equipamento=?,nome_equipamento=?,marca=?,modelo=?,numero_serie=?,descricao_problema=?,descricao_reparo=?,nome_tecnico=?,valor=? where os=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, tipo);
            pst.setString(2, jCboOsSituacao.getSelectedItem().toString());
            pst.setString(3, jTxtOsEquipamento.getText());
            pst.setString(4, jTxtOsMarca.getText());
            pst.setString(5, jTxtOsModelo.getText());
            pst.setString(6, jTxtOsNumeroSerie.getText());
            pst.setString(7, jTxtOsDefeito.getText());
            pst.setString(8, jTxtOsReparo.getText());
            pst.setString(9, jTxtOsTecnico.getText());
            pst.setString(10, jTxtOsValor.getText().replace(",", ".")); //Substitui a vírgula pelo Ponto
            pst.setString(11, jTxtOs.getText());
            // Validação dos campos obrigatórios
            if (jTxtClienteId.getText().isEmpty() || (jTxtOsEquipamento).getText().isEmpty()
                    || (jTxtOsTecnico).getText().isEmpty()
                    || (jCboOsSituacao).getSelectedItem().equals(" ")) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0);
                JOptionPane.showMessageDialog(null, "OS alterada com sucesso!");
                limpar();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void excluir_os() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir esta OS?", "Atenção!", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tbos where os=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, jTxtOs.getText());
                int apagado = pst.executeUpdate();
                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "OS excluída com sucesso");
                    limpar();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    // Limpar campos e gerenciar botões
    private void limpar() {
        jTxtOs.setText(null);
        jTxtData.setText(null);
        jTxtClienteId.setText(null);
        jTxtClientePesquisar.setText(null);
        ((DefaultTableModel) jTblClientes.getModel()).setRowCount(0);
        jCboOsSituacao.setSelectedItem(" ");
        jTxtOsEquipamento.setText(null);
        jTxtOsMarca.setText(null);
        jTxtOsModelo.setText(null);
        jTxtOsNumeroSerie.setText(null);
        jTxtOsDefeito.setText(null);
        jTxtOsReparo.setText(null);
        jTxtOsTecnico.setText(null);
        jTxtOsValor.setText("0");

        // Habilita o botão adicionar
        jBtnOsCreate.setEnabled(true);
        jBtnOsRead.setEnabled(true);
        // Desabilitar os botões
        jBtnOsDelete.setEnabled(false);
        jBtnOsUpdate.setEnabled(false);
        jBtnOsImprimir.setEnabled(false);
    }

    // Método para imprimir uma OS
    private void imprimir_os() {
        // Imprimindo uma OS
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressão desta OS?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            // Imprimindo relatório com o framework JasperReports
            try {
                // Usando a classe HashMap para criar um filtro
                HashMap filtro = new HashMap();
                filtro.put("os", Integer.parseInt(jTxtOs.getText()));
                // Usando a classe JasperPrint para preparar a impressão de um relatório
                JasperPrint print = JasperFillManager.fillReport("C:\\Reports\\Os.jasper", filtro, conexao);
                // A linha abaixo exibe o relatório atraves da classe JasperViewer
                JasperViewer.viewReport(print, false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    // Método de recuperação da Os
    private void recuperar_os() {
        String sql = "select max(os) from tbos";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            if(rs.next()){
                jTxtOs.setText(rs.getString(1));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTxtOs = new javax.swing.JTextField();
        jTxtData = new javax.swing.JTextField();
        jRbtnOrcamento = new javax.swing.JRadioButton();
        jRbtnOs = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jCboOsSituacao = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jTxtClientePesquisar = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTblClientes = new javax.swing.JTable();
        jTxtClienteId = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTxtOsValor = new javax.swing.JTextField();
        jTxtOsReparo = new javax.swing.JTextField();
        jTxtOsNumeroSerie = new javax.swing.JTextField();
        jTxtOsMarca = new javax.swing.JTextField();
        jTxtOsEquipamento = new javax.swing.JTextField();
        jTxtOsModelo = new javax.swing.JTextField();
        jTxtOsDefeito = new javax.swing.JTextField();
        jTxtOsTecnico = new javax.swing.JTextField();
        jBtnOsCreate = new javax.swing.JButton();
        jBtnOsRead = new javax.swing.JButton();
        jBtnOsUpdate = new javax.swing.JButton();
        jBtnOsDelete = new javax.swing.JButton();
        jBtnOsImprimir = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("OS");
        setPreferredSize(new java.awt.Dimension(720, 470));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setPreferredSize(new java.awt.Dimension(300, 110));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Nº Os");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Data");

        jTxtOs.setEditable(false);
        jTxtOs.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTxtOs.setEnabled(false);

        jTxtData.setEditable(false);
        jTxtData.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTxtData.setEnabled(false);

        buttonGroup1.add(jRbtnOrcamento);
        jRbtnOrcamento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jRbtnOrcamento.setText("Orçamento");
        jRbtnOrcamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRbtnOrcamentoActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRbtnOs);
        jRbtnOs.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jRbtnOs.setText("Ordem de Serviço");
        jRbtnOs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRbtnOsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jTxtOs, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jTxtData, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRbtnOrcamento)
                        .addGap(18, 18, 18)
                        .addComponent(jRbtnOs)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTxtOs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRbtnOrcamento)
                    .addComponent(jRbtnOs))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Situação");

        jCboOsSituacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jCboOsSituacao.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Aguardando Análise.", "Em Análise.", "Aguardando Aprovação.", "Aprovado pelo Cliente.", "Reparo em Andamento.", "Aguardando Peças.", "Conserto Concluído.", "Aguardando Retirada.", "Equipamento Entregue.", "Reparo Não Aprovado.", "Cancelado.", "Retornou." }));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(360, 180));

        jTxtClientePesquisar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTxtClientePesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTxtClientePesquisarKeyReleased(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/os/icones/readIcone24px.png"))); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("* ID");

        jTblClientes.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Nome", "Telefone"
            }
        ));
        jTblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTblClientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTblClientes);

        jTxtClienteId.setEditable(false);
        jTxtClienteId.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTxtClienteId.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTxtClientePesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTxtClienteId))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(jTxtClienteId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTxtClientePesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("* Equipamento");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Marca");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("Modelo");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("Nº Série");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setText("Defeito");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setText("Reparo");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel12.setText("* Técnico");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel13.setText("Valor Total");

        jTxtOsValor.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTxtOsValor.setText("0");

        jTxtOsReparo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jTxtOsNumeroSerie.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jTxtOsMarca.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jTxtOsEquipamento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jTxtOsModelo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jTxtOsDefeito.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jTxtOsTecnico.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jBtnOsCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/os/icones/createIcone.png"))); // NOI18N
        jBtnOsCreate.setToolTipText("Adicionar");
        jBtnOsCreate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBtnOsCreate.setPreferredSize(new java.awt.Dimension(80, 80));
        jBtnOsCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnOsCreateActionPerformed(evt);
            }
        });

        jBtnOsRead.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/os/icones/readIcone.png"))); // NOI18N
        jBtnOsRead.setToolTipText("Pesquisar");
        jBtnOsRead.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBtnOsRead.setPreferredSize(new java.awt.Dimension(80, 80));
        jBtnOsRead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnOsReadActionPerformed(evt);
            }
        });

        jBtnOsUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/os/icones/updateIcone.png"))); // NOI18N
        jBtnOsUpdate.setToolTipText("Editar");
        jBtnOsUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBtnOsUpdate.setEnabled(false);
        jBtnOsUpdate.setPreferredSize(new java.awt.Dimension(80, 80));
        jBtnOsUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnOsUpdateActionPerformed(evt);
            }
        });

        jBtnOsDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/os/icones/deleteIcone.png"))); // NOI18N
        jBtnOsDelete.setToolTipText("Deletar");
        jBtnOsDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBtnOsDelete.setEnabled(false);
        jBtnOsDelete.setPreferredSize(new java.awt.Dimension(80, 80));
        jBtnOsDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnOsDeleteActionPerformed(evt);
            }
        });

        jBtnOsImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/os/icones/printerIcone.png"))); // NOI18N
        jBtnOsImprimir.setToolTipText("Imprimir OS");
        jBtnOsImprimir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBtnOsImprimir.setEnabled(false);
        jBtnOsImprimir.setPreferredSize(new java.awt.Dimension(80, 80));
        jBtnOsImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnOsImprimirActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton1.setText("Limpar Campos");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(10, 10, 10)
                                        .addComponent(jCboOsSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTxtOsEquipamento))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel8)
                                        .addGap(44, 44, 44)
                                        .addComponent(jTxtOsModelo, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel12)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(jLabel10)
                                                .addGap(44, 44, 44)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTxtOsTecnico, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTxtOsDefeito, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(40, 40, 40)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel13))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jTxtOsReparo, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTxtOsMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTxtOsNumeroSerie, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTxtOsValor, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton1))))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jBtnOsCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jBtnOsRead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jBtnOsUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jBtnOsDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jBtnOsImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel3))
                            .addComponent(jCboOsSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jTxtOsEquipamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jTxtOsModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTxtOsDefeito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel10)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTxtOsMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTxtOsNumeroSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTxtOsReparo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTxtOsTecnico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTxtOsValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13)
                            .addComponent(jButton1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel12)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBtnOsCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnOsRead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnOsUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnOsDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnOsImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        setBounds(0, 0, 720, 470);
    }// </editor-fold>//GEN-END:initComponents

    private void jBtnOsCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnOsCreateActionPerformed
        // Chama o método cadastrar os
        cadastrar_os();
    }//GEN-LAST:event_jBtnOsCreateActionPerformed

    private void jBtnOsReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnOsReadActionPerformed
        // chama o método pesquisar os
        pesquisar_os();
    }//GEN-LAST:event_jBtnOsReadActionPerformed

    private void jBtnOsUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnOsUpdateActionPerformed
        // Chama o método alterar
        alterar_os();
    }//GEN-LAST:event_jBtnOsUpdateActionPerformed

    private void jBtnOsDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnOsDeleteActionPerformed
        // chama o método excluir
        excluir_os();
    }//GEN-LAST:event_jBtnOsDeleteActionPerformed

    private void jTxtClientePesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxtClientePesquisarKeyReleased
        // Equanto digitar chama o método pesquisar_cliente
        pesquisar_cliente();
    }//GEN-LAST:event_jTxtClientePesquisarKeyReleased

    private void jTblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTblClientesMouseClicked
        // Ao clicar na tabela chama o método setar_campos
        setar_campos();
    }//GEN-LAST:event_jTblClientesMouseClicked

    private void jRbtnOrcamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRbtnOrcamentoActionPerformed
        // Atribuindo um texto a variável tipo se selecionado
        tipo = "Orçamento";
    }//GEN-LAST:event_jRbtnOrcamentoActionPerformed

    private void jRbtnOsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRbtnOsActionPerformed
        // Atribuindo um texto a variável tipo se selecionado
        tipo = "OS";
    }//GEN-LAST:event_jRbtnOsActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // Ao abrir o form e marcar o radio button Orçamento
        jRbtnOrcamento.setSelected(true);
        tipo = "Orçamento";
    }//GEN-LAST:event_formInternalFrameOpened

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Limpa os campos
        limpar();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jBtnOsImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnOsImprimirActionPerformed
        // Chama o método imprimir_os
        imprimir_os();
    }//GEN-LAST:event_jBtnOsImprimirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jBtnOsCreate;
    private javax.swing.JButton jBtnOsDelete;
    private javax.swing.JButton jBtnOsImprimir;
    private javax.swing.JButton jBtnOsRead;
    private javax.swing.JButton jBtnOsUpdate;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jCboOsSituacao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRbtnOrcamento;
    private javax.swing.JRadioButton jRbtnOs;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTblClientes;
    private javax.swing.JTextField jTxtClienteId;
    private javax.swing.JTextField jTxtClientePesquisar;
    private javax.swing.JTextField jTxtData;
    private javax.swing.JTextField jTxtOs;
    private javax.swing.JTextField jTxtOsDefeito;
    private javax.swing.JTextField jTxtOsEquipamento;
    private javax.swing.JTextField jTxtOsMarca;
    private javax.swing.JTextField jTxtOsModelo;
    private javax.swing.JTextField jTxtOsNumeroSerie;
    private javax.swing.JTextField jTxtOsReparo;
    private javax.swing.JTextField jTxtOsTecnico;
    private javax.swing.JTextField jTxtOsValor;
    // End of variables declaration//GEN-END:variables
}

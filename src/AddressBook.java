import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.*;

public class AddressBook {

    private JTextField telTextField, nameTextField;
    private JTextArea jTextAreaShow;
    private HashMap<String, String> telMap;
    private File addressBookFile;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private String POP_UPS_TITLE_INFO = "提示";
    private String POP_UPS_TITLE_ERROR = "提示";

    private File createaddressBookFile() {
        File file = new File("D:/通讯录.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private AddressBook() {
        JFrame addrBookJFrame;
        JButton queryButton, addButton, deleteButton, clsButton;
        JLabel jLabelTel, jLabelName;

        final String ERROR_MESSAGE = "程序出错，联系程序员爸爸吧！！！";
        addressBookFile = createaddressBookFile();
        try {
            ois = new ObjectInputStream(new FileInputStream(addressBookFile));
            telMap = (HashMap<String, String>) ois.readObject();
        } catch (EOFException e) {
            telMap = new HashMap<String, String>();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, ERROR_MESSAGE, POP_UPS_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        }
        addrBookJFrame = new JFrame("通讯录V1.0") {
            @Override
            protected void processWindowEvent(WindowEvent e) {
                //这里需要对进来的WindowEvent进行判断，因为，不仅会有窗口关闭的WindowEvent进来，还可能有其他的WindowEvent进来
                if (e.getID() == WindowEvent.WINDOW_CLOSING) {
                    //  int option = JOptionPane.showConfirmDialog(null, "是否关闭程序？", "程序退出提示", JOptionPane.OK_CANCEL_OPTION);
                    //   if (option == JOptionPane.OK_OPTION) {
                    try {
                        oos = new ObjectOutputStream(new FileOutputStream(addressBookFile));
                        oos.writeObject(telMap);
                    } catch (Exception ee) {
                        JOptionPane.showMessageDialog(null, ERROR_MESSAGE, POP_UPS_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
                    } finally {
                        try {
                            if (ois != null) {
                                ois.close();
                            }
                            if (oos != null) {
                                oos.close();
                            }
                        } catch (Exception ee) {
                            JOptionPane.showMessageDialog(null, ERROR_MESSAGE, POP_UPS_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    super.processWindowEvent(e);
                    //    } else {
                    //用户选择不退出本程序，因此可以继续留在本窗口
                    //      }
                } else {
                    super.processWindowEvent(e);
                }
            }
        };
        addrBookJFrame.setLayout(new FlowLayout());
        jLabelTel = new JLabel("电话:");
        jLabelName = new JLabel("姓名:");
        telTextField = new JTextField(9);
        nameTextField = new JTextField(9);
        queryButton = new JButton("查询");
        addButton = new JButton("添加");
        deleteButton = new JButton("删除");
        clsButton = new JButton("清空输入");
        jTextAreaShow = new JTextArea(13, 28);
        jTextAreaShow.setFont(new Font("宋体", Font.PLAIN, 20));
        jTextAreaShow.setEditable(false);
        //jTextAreaShow.setText("姓 名             电 话\n");
        addrBookJFrame.add(jLabelName);
        addrBookJFrame.add(nameTextField);
        addrBookJFrame.add(jLabelTel);
        addrBookJFrame.add(telTextField);
        addrBookJFrame.add(queryButton);
        addrBookJFrame.add(addButton);
        addrBookJFrame.add(deleteButton);
        addrBookJFrame.add(clsButton);
        addrBookJFrame.add(jTextAreaShow);
        queryButton.addActionListener(new Monitor());
        addButton.addActionListener(new Monitor());
        deleteButton.addActionListener(new Monitor());
        clsButton.addActionListener(new Monitor());
        addrBookJFrame.setSize(350, 447);
        addrBookJFrame.setLocationRelativeTo(null);
        addrBookJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addrBookJFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new AddressBook();
    }

    private class Monitor implements ActionListener {

        private String ERROR_MESSAGE_TEL = "不输入号码，你保存什么？";
        private String ERROR_MESSAGE_NAME = "你能不能别这样，姓名都不输入吗？";
        private String ERROR_MESSAGE_ADD = "保存失败，这个号码已经存在了，懂？";
        private String ERROR_MESSAGE_DEL = "删除失败，姓名号码至少要输入一个，会员尊享删除全部数据功能！！";
        private String SUCCESS_MESSAGE_ADD = "你真是个天才，保存成功了！";
        private String SUCCESS_MESSAGE_DEL = "好吧，删除成功了！";
        private String SUCCESS_MESSAGE_QUERY = "没有号码了，快去保存一点吧！";

        public void actionPerformed(ActionEvent e) {
            String telStr = telTextField.getText();
            String nameStr = nameTextField.getText();
            String actionCommand = e.getActionCommand();
            if ("查询".equals(actionCommand)) {
                if ((telStr == null || telStr.length() == 0) && (nameStr == null || nameStr.length() == 0)) {
                    query(telMap);
                } else {
                    queryByNameOrTel(telMap, telStr, nameStr);
                }
                return;
            }
            if ("添加".equals(actionCommand)) {

                if (telStr == null || telStr.length() == 0) {
                    JOptionPane.showMessageDialog(null, ERROR_MESSAGE_TEL, POP_UPS_TITLE_INFO, JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (nameStr == null || nameStr.length() == 0) {
                    JOptionPane.showMessageDialog(null, ERROR_MESSAGE_NAME, POP_UPS_TITLE_INFO, JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!Pattern.matches("^[0-9]*$", telStr)) {
                    JOptionPane.showMessageDialog(null, "你真是个聪明，电话号码只能是数字啊！！", POP_UPS_TITLE_INFO, JOptionPane.ERROR_MESSAGE);
                }

                if (telStr.length() > 11) {
                    JOptionPane.showMessageDialog(null, "哎，号码不能大于11位啊！！", POP_UPS_TITLE_INFO, JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (nameStr.length() > 9) {
                    JOptionPane.showMessageDialog(null, "别搞些花里胡哨的名字，姓名不能大于9位！！", POP_UPS_TITLE_INFO, JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!isTelInTelMap(telMap, telStr)) {
                    telMap.put(telStr, nameStr);
                    JOptionPane.showMessageDialog(null, SUCCESS_MESSAGE_ADD, POP_UPS_TITLE_INFO, JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, ERROR_MESSAGE_ADD, POP_UPS_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
                }
                query(telMap);
                return;
            }
            if ("删除".equals(actionCommand)) {
                if (isTelInTelMap(telMap, telStr)) {
                    telMap.remove(telStr);
                    JOptionPane.showMessageDialog(null, SUCCESS_MESSAGE_DEL, POP_UPS_TITLE_INFO, JOptionPane.INFORMATION_MESSAGE);
                } else if (isNameInTelMap(telMap, nameStr)) {
                    for (String key : telMap.keySet()) {
                        if (nameStr.equals(telMap.get(key))) {
                            telMap.remove(key);
                        }
                    }
                    JOptionPane.showMessageDialog(null, SUCCESS_MESSAGE_DEL, POP_UPS_TITLE_INFO, JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, ERROR_MESSAGE_DEL, POP_UPS_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
                }
                query(telMap);
                return;
            }
            if ("清空输入".equals(actionCommand)) {
                telTextField.setText("");
                nameTextField.setText("");
                return;
            }
        }

        private void query(Map<String, String> map) {
            jTextAreaShow.setText("");
            if (map != null && map.size() > 0) {
                for (String key : map.keySet()) {
                    jTextAreaShow.append(map.get(key) + "   " + key + "\n");
                }
            } else {
                JOptionPane.showMessageDialog(null, SUCCESS_MESSAGE_QUERY, POP_UPS_TITLE_INFO, JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private void queryByNameOrTel(Map<String, String> map, String tel, String name) {
            jTextAreaShow.setText("");
            if (map != null && map.size() > 0) {
                for (String key : map.keySet()) {
                    if (key.equals(tel) || map.get(key).equals(name)) {
                        jTextAreaShow.append(map.get(key) + "   " + key + "\n");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, SUCCESS_MESSAGE_QUERY, POP_UPS_TITLE_INFO, JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private boolean isTelInTelMap(Map<String, String> map, String str) {
            if (str == null || str.length() == 0) {
                return false;
            }
            if (map != null && map.size() > 0) {
                for (String key : map.keySet()) {
                    if (str.equals(key)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean isNameInTelMap(Map<String, String> map, String str) {
            if (str == null || str.length() == 0) {
                return false;
            }
            if (map != null && map.size() > 0) {
                for (String key : map.keySet()) {
                    if (str.equals(map.get(key))) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}





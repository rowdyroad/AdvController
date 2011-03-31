package Registrator;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		 
        JFrame f = new JFrame ("Hello, World!");

        // ����� �������������� ���������: ���������� listener � ���������������
        // �� ���������� �������� ����, ������� ���������� �� windowClosing() 
        // �������������� ���������� ����������� ������ ������� System.exit()
        // ������ �� ���� ����� "����������" ������ ����� ������� �� �������� ����.
        // ������ ������ ���������� ������� ����, �� �� ������������� ����������. ���
        // ����� ���������� ����� �������� ���� �� ����� ������� ��� ����.

        f.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);

        // ������ ����� ������ � ���:
        //            f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

        // ��������� �� ������ ���� ��������������� ��������� � �������.

        //f.getContentPane().add (new JLabel("Hello, World!")); - ������ �����
        f.add(new JLabel("Hello World"));

        // pack() "�����������" ���� �� ������������ �������, ������������� �� ��������� �������� 
        // ���� ������������� � ��� �����������.

        f.pack();

        // �������� ����

        f.setVisible(true);
	}

}

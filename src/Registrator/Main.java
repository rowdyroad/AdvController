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

        // –анее практиковалось следующее: создавалс€ listener и регистрировалс€
        // на экземпл€ре главного окна, который реагировал на windowClosing() 
        // принудительной остановкой виртуальной машины вызовом System.exit()
        // “еперь же есть более "правильный" способ задав реакцию на закрытие окна.
        // ƒанный способ уничтожает текущее окно, но не останавливает приложение. “ем
        // самым приложение будет работать пока не будут закрыты все окна.

        f.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);

        // однако можно задать и так:
        //            f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

        // ƒобавл€ем на панель окна нередактируемый компонент с текстом.

        //f.getContentPane().add (new JLabel("Hello, World!")); - старый стиль
        f.add(new JLabel("Hello World"));

        // pack() "упаковывает" окно до оптимального размера, рассчитанного на основании размеров 
        // всех расположенных в нем компонентов.

        f.pack();

        // ѕоказать окно

        f.setVisible(true);
	}

}

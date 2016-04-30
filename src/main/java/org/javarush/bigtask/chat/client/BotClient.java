package org.javarush.bigtask.chat.client;

import org.javarush.bigtask.chat.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Alexey on 02.03.2016.
 */
public class BotClient extends Client
{
    public class BotSocketThread extends SocketThread
    {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException
        {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message)
        {
            ConsoleHelper.writeMessage(message);
            String[] s = message.split(": ");
            if (s == null || s.length < 2) return;
            switch (s[1])
            {
                case "дата":
                    sendTextMessage(String.format("Информация для %s: %s", s[0], new SimpleDateFormat("d.MM.YYYY").format(Calendar.getInstance().getTime())));
                    break;
                case "день":
                    sendTextMessage(String.format("Информация для %s: %s", s[0], new SimpleDateFormat("d").format(Calendar.getInstance().getTime())));
                    break;
                case "месяц":
                    sendTextMessage(String.format("Информация для %s: %s", s[0], new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime())));
                    break;
                case "год":
                    sendTextMessage(String.format("Информация для %s: %s", s[0], new SimpleDateFormat("YYYY").format(Calendar.getInstance().getTime())));
                    break;
                case "время":
                    sendTextMessage(String.format("Информация для %s: %s", s[0], new SimpleDateFormat("H:mm:ss").format(Calendar.getInstance().getTime())));
                    break;
                case "час":
                    sendTextMessage(String.format("Информация для %s: %s", s[0], new SimpleDateFormat("H").format(Calendar.getInstance().getTime())));
                    break;
                case "минуты":
                    sendTextMessage(String.format("Информация для %s: %s", s[0], new SimpleDateFormat("m").format(Calendar.getInstance().getTime())));
                    break;
                case "секунды":
                    sendTextMessage(String.format("Информация для %s: %s", s[0], new SimpleDateFormat("s").format(Calendar.getInstance().getTime())));
                    break;

            }
        }
    }

    @Override
    protected SocketThread getSocketThread()
    {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSentTextFromConsole()
    {
        return false;
    }

    @Override
    protected String getUserName()
    {
        return "date_bot_" + (int) (Math.random()*100);
    }


    public static void main(String[] args)
    {
        new BotClient().run();
    }
}

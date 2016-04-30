package org.javarush.bigtask.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Alexey on 01.03.2016.
 */
public class Server
{
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>(10);

    private static class Handler extends Thread
    {
        private Socket socket;

        public Handler(Socket socket)
        {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException
        {
            while (true)
            {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message message = connection.receive();
                if (MessageType.USER_NAME == message.getType())
                    if (message.getData() != null && !"".equals(message.getData()))
                    {
                        String userName = message.getData();
                        if (!connectionMap.containsKey(userName))
                        {
                            connectionMap.put(userName, connection);
                            connection.send(new Message(MessageType.NAME_ACCEPTED));
                            return userName;
                        }
                    }
            }

        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException
        {
            for (Map.Entry<String, Connection> m : connectionMap.entrySet())
            {
                if (!m.getKey().equals(userName))
                {
                    connection.send(new Message(MessageType.USER_ADDED, m.getKey()));
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException
        {
            while (true)
            {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT)
                {
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + message.getData()));
                } else ConsoleHelper.writeMessage("сообщение не является текстом");
            }
        }

        public void run()
        {
            ConsoleHelper.writeMessage("установлено новое соединение с удаленным адресом:"
                    + socket.getRemoteSocketAddress());
            String clientName = null;
            try (Connection connection = new Connection(socket))
            {
                clientName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, clientName));
                sendListOfUsers(connection, clientName);
                serverMainLoop(connection, clientName);
            }
            catch (IOException | ClassNotFoundException ignory)
            {
                ConsoleHelper.writeMessage("Произошла ошибка при обмене данными с удаленным адресом");
            }
            if (clientName != null)
            {
                connectionMap.remove(clientName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, clientName));
            }
            ConsoleHelper.writeMessage("соединение с удаленным адресом закрыто");
        }
    }

    public static void main(String[] args)
    {
        ConsoleHelper.writeMessage("port:");
        int serverPort = ConsoleHelper.readInt();
        ServerSocket s = null;
        try
        {
            s = new ServerSocket(serverPort);
            ConsoleHelper.writeMessage("Cервер запущен.");
            while (true)
            {
                new Handler(s.accept()).start();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (s != null) try
            {
                s.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    public static void sendBroadcastMessage(Message message)
    {

        for (Map.Entry<String, Connection> m : connectionMap.entrySet())
        {
            try
            {
                m.getValue().send(message);
            }
            catch (IOException e)
            {
                ConsoleHelper.writeMessage("не смогли отправить сообщение");
            }
        }
    }

}

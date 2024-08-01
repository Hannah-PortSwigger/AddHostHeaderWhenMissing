import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;

import javax.swing.*;

import java.awt.*;

import static burp.api.montoya.http.HttpService.httpService;
import static burp.api.montoya.proxy.http.ProxyRequestReceivedAction.continueWith;
import static javax.swing.JOptionPane.showInputDialog;

@SuppressWarnings("unused")
public class Extension implements BurpExtension
{
    @Override
    public void initialize(MontoyaApi montoyaApi)
    {
        montoyaApi.extension().setName("Add host header when missing");

        montoyaApi.proxy().registerRequestHandler(new ProxyRequestHandler()
        {
            @Override
            public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest)
            {
                if(!interceptedRequest.hasHeader("Host"))
                {
                    String url = showInputDialog(montoyaApi.userInterface().swingUtils().suiteFrame(), new PopupPanel(interceptedRequest));

                    HttpService service = httpService(url);
                    HttpRequest newRequest = interceptedRequest.withService(service).withHeader("Host", service.host());

                    return continueWith(newRequest);
                }

                return continueWith(interceptedRequest);
            }

            @Override
            public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest)
            {
                return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
            }
        });
    }

    private static class PopupPanel extends JPanel
    {
        public PopupPanel(HttpRequest interceptedRequest)
        {
            this.setLayout(new BorderLayout());

            JTextArea textArea = new JTextArea(interceptedRequest.toString(), 20, 60);
            JScrollPane scrollPane = new JScrollPane(textArea);

            this.add(scrollPane, BorderLayout.CENTER);
            this.add(new JLabel("Enter URL for request:"), BorderLayout.SOUTH);
        }
    }
}

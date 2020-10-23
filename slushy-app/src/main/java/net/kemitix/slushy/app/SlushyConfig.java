package net.kemitix.slushy.app;

public interface SlushyConfig {

    /**
     * The Trello Developer API Key.
     *
     * <p>https://trello.com/app-key</p>
     */
    String getTrelloKey();

    /**
     * The Trello Developer API Key Token.
     *
     * <p>https://trello.com/app-key</p>
     */
    String getTrelloSecret();

    /**
     * The name of the user with access to the Trello Slush Pile Board.
     */
    String getUserName();

    /**
     * The trello board containing the Slush Pile.
     */
    String getBoardName();

    /**
     * The email address to send submission attachments to.
     *
     * <p>e.g. the kindle address</p>
     */
    String getSender();

    /**
     * The email address to send emails from.
     *
     * <p>If sending to Kindle, then ensure this address is listed as a valid sender.</p>
     */
    String getReader();

    /**
     * The URL of the Webhook to register with services.
     */
    String getWebhook();

}

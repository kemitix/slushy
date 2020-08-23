package net.kemitix.slushy.app;

import org.odftoolkit.odfdom.converter.xhtml.XHTMLConverter;
import org.odftoolkit.odfdom.converter.xhtml.XHTMLOptions;
import org.odftoolkit.odfdom.doc.OdfTextDocument;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

@ApplicationScoped
public class OdtToHtmlAttachmentConverter
        implements AttachmentConverter {

    private static final Logger LOG =
            Logger.getLogger(
                    OdtToHtmlAttachmentConverter.class.getName());

    @Inject
    AttachmentDirectory attachmentDirectory;

    @Override
    public boolean canHandle(Attachment attachment) {
        return attachment.getFileName()
                .getName()
                .endsWith(".odt");
    }

    @Override
    public Optional<Attachment> convert(Attachment attachment) {
        Attachment localAttachment = attachment.download();
        String name = localAttachment.getFileName().getName();
        LOG.info("Converting from " + name);
        String htmlName = name.substring(0, name.length() - 3) + "html";
        File htmlFile = attachmentDirectory.createFile(new File(htmlName));
        LOG.info("Converting  to  " + htmlFile.getAbsolutePath());
        try (
            InputStream in = new FileInputStream(localAttachment.getFileName());
            OutputStream out = new FileOutputStream(htmlFile);
        ) {
            OdfTextDocument document = OdfTextDocument.loadDocument(in);
            XHTMLConverter.getInstance().convert(document, out, XHTMLOptions.create());
            if (htmlFile.exists()) {
                return Optional.of(new LocalAttachment(htmlFile));
            } else {
                throw new FileNotFoundException(htmlFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Stream<String> canConvertFrom() {
        return Stream.of("odt");
    }

}

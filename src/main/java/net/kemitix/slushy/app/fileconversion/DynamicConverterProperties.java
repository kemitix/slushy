package net.kemitix.slushy.app.fileconversion;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.kemitix.slushy.app.config.DynamicConfig;

@ApplicationScoped
public class DynamicConverterProperties
        implements CalibreConverterProperties, DynamicConfig {

    @Inject
    CalibraConverterConfigMapping configMapping;

    @Override
    public boolean smartenPunctuation() {
        return findBooleanValue(CalibraConverterConfigMapping.PREFIX, SMARTEN_PUNCTUATION)
                .orElseGet(configMapping::smartenPunctuation);
    }

    @Override
    public String changeJustification() {
        return findValue(CalibraConverterConfigMapping.PREFIX, CHANGE_JUSTIFICATION)
                .orElseGet(configMapping::changeJustification);
    }

    @Override
    public boolean insertBlankLine() {
        return findBooleanValue(CalibraConverterConfigMapping.PREFIX, INSERT_BLANK_LINE)
                .orElseGet(configMapping::insertBlankLine);
    }

    @Override
    public boolean removeParagraphSpacing() {
        return findBooleanValue(CalibraConverterConfigMapping.PREFIX, REMOVE_PARAGRAPH_SPACING)
                .orElseGet(configMapping::removeParagraphSpacing);
    }

    @Override
    public boolean enableHeuristics() {
        return findBooleanValue(CalibraConverterConfigMapping.PREFIX, ENABLE_HEURISTICS)
                .orElseGet(configMapping::enableHeuristics);
    }

    @Override
    public boolean insertMetadata() {
        return findBooleanValue(CalibraConverterConfigMapping.PREFIX, INSERT_METADATA)
                .orElseGet(configMapping::insertMetadata);
    }

    @Override
    public boolean useAutoToc() {
        return findBooleanValue(CalibraConverterConfigMapping.PREFIX, USE_AUTO_TOC)
                .orElseGet(configMapping::useAutoToc);
    }
}

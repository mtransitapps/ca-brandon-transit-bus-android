package org.mtransit.parser.ca_brandon_transit_bus;

import static org.mtransit.commons.RegexUtils.DIGITS;
import static org.mtransit.commons.StringUtils.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CharUtils;
import org.mtransit.commons.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// http://opendata.brandon.ca/
// http://opendata.brandon.ca/Transit/google_transit.zip
public class BrandonTransitBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new BrandonTransitBusAgencyTools().start(args);
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "Brandon Transit";
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return true;
	}

	@Override
	public long getRouteId(@NotNull GRoute gRoute) {
		if ("IND".equals(gRoute.getRouteShortName())) {
			return 9001L;
		}
		return super.getRouteId(gRoute);
	}

	@NotNull
	@Override
	public String getRouteShortName(@NotNull GRoute gRoute) {
		final String rsnS = gRoute.getRouteShortName();
		if (!CharUtils.isDigitsOnly(rsnS)) {
			final Matcher matcher = DIGITS.matcher(rsnS);
			if (matcher.find()) {
				return matcher.group();
			}
		}
		return super.getRouteShortName(gRoute);
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.cleanNumbers(routeLongName);
		routeLongName = CleanUtils.cleanStreetTypes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	private static final String AGENCY_COLOR_BLUE = "00B8F1"; // BLUE (from web site CSS)

	private static final String AGENCY_COLOR = AGENCY_COLOR_BLUE;

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@Override
	public @Nullable String provideMissingRouteColor(@NotNull GRoute gRoute) {
		final String rnsS = gRoute.getRouteShortName();
		if (CharUtils.isDigitsOnly(rnsS)) {
			final int rsn = Integer.parseInt(rnsS);
			switch (rsn) {
			// @formatter:off
			case 4: return "409AED";
			case 5: return "A83800";
			case 8: return "F8E208"; // "FAEB52";
			case 14: return "960096";
			case 15: return "0070FF";
			case 16: return "66C7EB";
			case 17: return "FF00C4";
			case 22: return "FFAB00";
			case 23: return "73B373";
			// @formatter:on
			}
		}
		if ("IND".equals(rnsS)) {
			return "4F4C4C";
		}
		throw new MTLog.Fatal("Unexpected route color for %s!", gRoute.toStringPlus());
	}

	@Override
	public boolean directionSplitterEnabled(long routeId) {
		return false; // 2024-04-16: actually not working TODO try again later?
	}

	@Override
	public boolean allowNonDescriptiveHeadSigns(long routeId) {
		if (routeId == 4L) { // 2024-04-16
			return true;
		}
		if (routeId == 17L) { // 2024-04-16
			return true;
		}
		return super.allowNonDescriptiveHeadSigns(routeId);
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern ENDS_WITH_RETURN_ = Pattern.compile("( \\(return\\))", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = ENDS_WITH_RETURN_.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.cleanBounds(tripHeadsign);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	@Override
	public int getStopId(@NotNull GStop gStop) {
		final String stopCode = gStop.getStopCode(); // use stop code as ID
		if (CharUtils.isDigitsOnly(stopCode)) {
			return Integer.parseInt(stopCode);
		}
		return super.getStopId(gStop);
	}
}
